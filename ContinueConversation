import boto3
import json
from datetime import datetime
from decimal import Decimal

# Initialize AWS services
dynamodb = boto3.resource('dynamodb')
sagemaker_runtime = boto3.client('sagemaker-runtime')
lambda_client = boto3.client('lambda')

# Define the DynamoDB tables
chat_history_table = dynamodb.Table('ChatHistory')
products_table = dynamodb.Table('Products')
persona_progress_table = dynamodb.Table('PersonaProgress')

# Define the SageMaker endpoint
ENDPOINT_NAME = 'jumpstart-dft-hf-llm-nvidia-llama3-20241115-072526'  # Replace with your actual endpoint name

def lambda_handler(event, context):
    # Logging the received event for debugging
    print("Received event:", event)
    
    # Attempt to parse 'body' for both API Gateway and direct Lambda invocation scenarios
    if 'body' in event:
        try:
            # If body is a string (from API Gateway), parse it as JSON
            body = json.loads(event['body']) if isinstance(event['body'], str) else event['body']
        except json.JSONDecodeError as e:
            print("JSON decode error:", e)
            return {
                "statusCode": 400,
                "body": json.dumps("Error: Invalid JSON in request body.")
            }
    else:
        # If 'body' is not in the event, assume the entire event is the input (Lambda test console case)
        body = event

    # Extract session_id and user_input from the parsed body
    session_id = body.get("session_id")
    salesperson_input = body.get("user_input")

    # Validate that both required fields are present
    if not session_id or not salesperson_input:
        print("Missing required fields: session_id or user_input")
        return {
            "statusCode": 400,
            "body": json.dumps("Error: 'session_id' and 'user_input' are required.")
        }

    # Retrieve session data, including level and product_id from ChatHistory using session_id
    try:
        response = chat_history_table.query(
            KeyConditionExpression=boto3.dynamodb.conditions.Key('session_id').eq(session_id),
            ScanIndexForward=True
        )
        if 'Items' in response and len(response['Items']) > 0:
            conversation_items = response['Items']
            product_id = conversation_items[0].get('ProductId', '')
            level = int(conversation_items[0].get('level', 1))
            conversation_messages = [
                f"Salesperson: {item['user_input']}" if item['user_input'] else f"Customer: {item['ai_response']}"
                for item in conversation_items if item['user_input'] or item['ai_response']
            ]
            conversation_messages.append(f"Salesperson: {salesperson_input}")
            conversation_history = "\n".join(conversation_messages[-10:]) + "\n"
        else:
            return {
                "statusCode": 400,
                "body": json.dumps("Error: Invalid 'session_id' or conversation not found.")
            }
    except Exception as e:
        print("Error fetching conversation from DynamoDB:", e)
        return {"statusCode": 500, "body": f"Error fetching conversation from DynamoDB: {str(e)}"}

    # Retrieve product and persona details from Products table
    try:
        product_response = products_table.get_item(Key={'ProductId': product_id})
        product_details = product_response['Item']
        persona_info = product_details.get('ProductLevels', {}).get(f'Level{level}', {}).get('Persona', {})
        persona_name = persona_info.get('Name', 'Customer')
        persona_description = persona_info.get('Description', '')
        product_name = product_details.get('ProductName', 'Product')
        product_description = product_details.get('ProductDescription', 'No description available.')
    except Exception as e:
        return {"statusCode": 500, "body": f"Error fetching product data from DynamoDB: {str(e)}"}

    # Generate the AI's response using SageMaker
    system_prompt = (
        f"As {persona_name}, you are {persona_description}. "
        f"You are interested in {product_name}, which is {product_description}. "
        f"At this level, you require more detailed information and convincing arguments. You must strictly behave as a customer only."
    )
    prompt_message = (
        f"{system_prompt}\n\n"
        f"Conversation history:\n"
        f"{conversation_history}"
        f"{persona_name}:"
    )

    payload = {
        "inputs": prompt_message,
        "parameters": {
            "max_new_tokens": 150,
            "temperature": 0.7,
            "top_p": 0.9,
            "stop": [f"{persona_name}:", "Salesperson:", "Customer:", "\n\n"]
        }
    }

    try:
        response = sagemaker_runtime.invoke_endpoint(
            EndpointName=ENDPOINT_NAME,
            ContentType="application/json",
            Body=json.dumps(payload)
        )
        response_body = response['Body'].read().decode('utf-8')
        
        # Print the raw response for debugging
        print("Raw SageMaker Response:", response_body)

        response_payload = json.loads(response_body)
        generated_text = response_payload[0].get('generated_text', '').strip() if isinstance(response_payload, list) else response_payload.get('generated_text', '').strip()

        # Extract AI response after the last salesperson message
        if persona_name + ":" in generated_text:
            ai_response = generated_text.split(persona_name + ":")[-1].strip()
        else:
            ai_response = generated_text

        # Remove trailing "\nSalesperson:" from the AI response if it exists
        if ai_response.endswith("\nSalesperson:"):
            ai_response = ai_response.rsplit("\nSalesperson:", 1)[0].strip()

        print("Extracted AI response:", ai_response)  # Log the extracted response

        if not ai_response:
            return {"statusCode": 500, "body": "Error: AI response is empty."}

        # Call analyze_sentiment to get conviction, mood, and convinced status
        sentiment_event = {"ai_response": ai_response, "session_id": session_id}
        sentiment_response = lambda_client.invoke(
            FunctionName="analyze_sentiment",
            InvocationType="RequestResponse",
            Payload=json.dumps(sentiment_event)
        )
        sentiment_data = json.loads(sentiment_response['Payload'].read().decode('utf-8'))["body"]
        sentiment_data = json.loads(sentiment_data)

        conviction_score = sentiment_data["conviction_score"]
        mood = sentiment_data["mood"]
        convinced = sentiment_data["convinced"]

        # Initialize levels_passed and handle progression logic
        levels_passed = []
        if convinced:
            # Add current level to levels_passed, reset progress and move to next level
            levels_passed.append(level)
            level += 1
            progress_percentage = 0
        else:
            # Track progress without changing levels
            progress_percentage = conviction_score

        # Update PersonaProgress table with the latest level and progress
        try:
            persona_progress_table.put_item(
                Item={
                    'UserId': 'AI_Customer',
                    'ProductId': product_id,
                    'LevelsPassed': levels_passed,
                    'ProgressPercentage': progress_percentage
                }
            )
        except Exception as e:
            return {"statusCode": 500, "body": f"Error saving progress to PersonaProgress table: {str(e)}"}

        # Save only the specified attributes to ChatHistory table
        try:
            chat_history_table.put_item(
                Item={
                    'session_id': session_id,
                    'timestamp': int(datetime.now().timestamp()),
                    'user_input': salesperson_input,
                    'ai_response': ai_response,
                    'ProductId': product_id,
                    'level': level
                }
            )
        except Exception as e:
            return {"statusCode": 500, "body": f"Error saving chat to DynamoDB: {str(e)}"}

        # Return final response
        response_data = {
            "session_id": session_id,
            "ai_response": ai_response,
            "conviction_score": conviction_score,
            "mood": mood,
            "convinced": convinced,
            "levels_passed": levels_passed,
            "current_level": level,
            "progress_percentage": progress_percentage
        }
        return {
            "statusCode": 200,
            "body": json.dumps(convert_decimal(response_data))
        }

    except Exception as e:
        return {"statusCode": 500, "body": f"Error processing sentiment analysis: {str(e)}"}

# Helper function to convert Decimal types in dictionaries to int or float
def convert_decimal(obj):
    if isinstance(obj, list):
        return [convert_decimal(item) for item in obj]
    elif isinstance(obj, dict):
        return {k: convert_decimal(v) for k, v in obj.items()}
    elif isinstance(obj, Decimal):
        return int(obj) if obj % 1 == 0 else float(obj)
    else:
        return obj
