import json
import boto3
import uuid
from datetime import datetime
from decimal import Decimal

# Initialize AWS services
dynamodb = boto3.resource('dynamodb')
sagemaker_runtime = boto3.client('sagemaker-runtime')

# Define the DynamoDB tables
products_table = dynamodb.Table('Products')
chat_history_table = dynamodb.Table('ChatHistory')
persona_progress_table = dynamodb.Table('PersonaProgress')

# Define the SageMaker endpoint
ENDPOINT_NAME = 'jumpstart-dft-hf-llm-nvidia-llama3-20241115-072526'  # Replace with your actual endpoint name

def lambda_handler(event, context):
    # Extract event details
    user_id = event.get("user_id", "")
    product_id = event.get("product_id", "")
    level = event.get("level", 1)  # Accept the level to start from, based on initialize_session
    reset = event.get("reset", False)
    
    if not user_id or not product_id:
        return {
            "statusCode": 400,
            "body": "Error: 'user_id' and 'product_id' are required."
        }

    # Handle reset logic
    if reset:
        try:
            # Reset progress for the product
            persona_progress_table.delete_item(
                Key={
                    'UserId': user_id,
                    'ProductId': product_id
                }
            )
            level = 1  # Restart from level 1 if reset is triggered
        except Exception as e:
            return {
                "statusCode": 500,
                "body": f"Error resetting progress: {str(e)}"
            }

    # Retrieve product details from Products table
    try:
        product_response = products_table.get_item(Key={'ProductId': product_id})
        product_details = product_response['Item']
    except Exception as e:
        return {
            "statusCode": 500,
            "body": f"Error fetching product data: {str(e)}"
        }

    # Prepare prompt and SageMaker payload based on level
    persona_info = product_details.get('ProductLevels', {}).get(f'Level{level}', {}).get('Persona', {})
    persona_name = persona_info.get('Name', 'Customer')
    primary_trait = persona_info.get('PrimaryTrait', 'Neutral')
    persona_description = persona_info.get('Description', '')
    product_name = product_details.get('ProductName', 'Product')
    product_description = product_details.get('ProductDescription', 'No description available.')

    system_prompt = (
        "This is a chat between a salesperson and a customer AI. "
        "The AI customer will ask relevant questions about the product to assess if it matches their interests."
    )
    context = (
        f"The customer is named {persona_name}, who is {primary_trait.lower()} and {persona_description.lower()}. "
        f"The product, {product_name}, offers {product_description}."
    )
    user_message = "Start the conversation as a customer interested in learning more about the product. Respond and behave as a real customer."
    prompt_message = f"System: {system_prompt}\n\nContext: {context}\n\nUser: {user_message}\n\nAssistant:"

    # SageMaker payload
    payload = {
        "inputs": prompt_message,
        "parameters": {
            "max_new_tokens": 100,
            "temperature": 0.7,
            "top_p": 0.9
        }
    }

    # Invoke SageMaker model
    try:
        response = sagemaker_runtime.invoke_endpoint(
            EndpointName=ENDPOINT_NAME,
            ContentType="application/json",
            Body=json.dumps(payload)
        )
        response_body = response['Body'].read().decode('utf-8')
        response_payload = json.loads(response_body)
        ai_response = response_payload[0].get('generated_text', '').split('Assistant:')[1].strip()

        # Generate a unique session_id for this conversation
        session_id = str(uuid.uuid4())

        # Save initial chat to ChatHistory table
        chat_history_table.put_item(
            Item={
                'session_id': session_id,
                'timestamp': int(datetime.now().timestamp()),
                'user_input': "",  # Initial input is blank as AI starts the conversation
                'ai_response': ai_response,
                'ProductId': product_id,
                'level': level
            }
        )

        return {
            "statusCode": 200,
            "body": json.dumps(convert_decimal({
                "session_id": session_id,
                "ai_response": ai_response,
                "level": level
            }))
        }
    except Exception as e:
        return {
            "statusCode": 500,
            "body": f"Error starting conversation: {str(e)}"
        }

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
