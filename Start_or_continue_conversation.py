import json
import boto3
from decimal import Decimal

# Initialize AWS services
lambda_client = boto3.client('lambda')
dynamodb = boto3.resource('dynamodb')

# Define DynamoDB table and Lambda function names
chat_history_table = dynamodb.Table('ChatHistory')
persona_progress_table = dynamodb.Table('PersonaProgress')
start_conversation_lambda = "StartConversation"  # Replace with actual Lambda name
continue_conversation_lambda = "Continueconversation"  # Replace with actual Lambda name

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

    # Extract necessary fields from the parsed body
    user_id = body.get("user_id")
    product_id = body.get("product_id")
    levels_passed = body.get("levels_passed", 0)
    progress_percentage = body.get("progress_percentage", 0)
    reset = body.get("reset", False)

    # Validate that required fields are present
    if not user_id or not product_id:
        print("Missing required fields: user_id or product_id")
        return {
            "statusCode": 400,
            "body": json.dumps("Error: 'user_id' and 'product_id' are required.")
        }

    # Logging extracted values for debugging
    print(f"Input - User ID: {user_id}, Product ID: {product_id}, Levels Passed: {levels_passed}, Progress Percentage: {progress_percentage}, Reset: {reset}")

    # Case 1: Reset - start a new conversation from Level 1
    if reset:
        print("Reset is true. Starting a new conversation from Level 1.")
        return invoke_start_conversation(user_id, product_id, 1)

    # Case 2: No levels passed but there is progress, so retrieve previous messages only
    if levels_passed == 0 and progress_percentage > 0:
        print("No levels passed, but progress exists. Fetching previous messages from Level 1.")
        return fetch_previous_chats(user_id, product_id, 1)

    # Case 3: Levels have been passed; determine next level to start or continue from
    if levels_passed > 0:
        next_level = levels_passed + 1
        if progress_percentage > 0:
            print(f"Level {levels_passed} passed with progress. Fetching previous messages from Level {next_level}.")
            return fetch_previous_chats(user_id, product_id, next_level)
        else:
            print(f"Level {levels_passed} passed but no progress. Starting new conversation at Level {next_level}.")
            return invoke_start_conversation(user_id, product_id, next_level)

    # Default: Start fresh at Level 1 if no progress or levels have been passed
    print("No progress and no levels passed. Starting conversation from Level 1.")
    return invoke_start_conversation(user_id, product_id, 1)

# Helper function to fetch previous chat messages without invoking the conversation
def fetch_previous_chats(user_id, product_id, level):
    try:
        # Fetch previous chat sessions for the user_id and product_id at the specified level
        response = chat_history_table.scan(
            FilterExpression=boto3.dynamodb.conditions.Attr('ProductId').eq(product_id) &
                             boto3.dynamodb.conditions.Attr('level').eq(level)
        )

        # Sort items by timestamp to get chat history in ascending order
        if 'Items' in response and response['Items']:
            latest_session = max(response['Items'], key=lambda x: x['timestamp'])
            session_id = latest_session['session_id']
            print(f"Fetching previous messages with session_id: {session_id}")
            
            # Retrieve all messages for the ongoing conversation in ascending order
            chat_history_response = chat_history_table.query(
                KeyConditionExpression=boto3.dynamodb.conditions.Key('session_id').eq(session_id),
                ScanIndexForward=True  # Ensure messages are in chronological order
            )
            
            # Collect all previous messages in the chat history
            previous_messages = []
            for item in chat_history_response.get('Items', []):
                previous_messages.append({
                    "user_input": item.get("user_input", ""),
                    "ai_response": item.get("ai_response", ""),
                    "timestamp": convert_decimal(item.get("timestamp", 0))
                })
            
            # Return only the chat history without calling ContinueConversation
            print(f"Returning previous chat messages for session_id {session_id}.")
            return {
                "statusCode": 200,
                "body": json.dumps({
                    "session_id": session_id,
                    "previous_messages": previous_messages
                })
            }
        
        else:
            # If no ongoing session found, start a new conversation
            print(f"No ongoing session found. Starting new conversation at Level {level}.")
            return invoke_start_conversation(user_id, product_id, level)
    
    except Exception as e:
        print(f"Error fetching session or previous messages: {e}")
        return {
            "statusCode": 500,
            "body": json.dumps(f"Error fetching previous messages: {str(e)}")
        }

# Function to invoke the StartConversation Lambda function
def invoke_start_conversation(user_id, product_id, level):
    try:
        response = lambda_client.invoke(
            FunctionName=start_conversation_lambda,
            InvocationType="RequestResponse",
            Payload=json.dumps({
                "user_id": user_id,
                "product_id": product_id,
                "level": level
            })
        )
        result = json.loads(response['Payload'].read())
        print(f"StartConversation response: {result}")
        return {
            "statusCode": 200,
            "body": json.dumps(convert_decimal(result))
        }
    except Exception as e:
        print(f"Error invoking StartConversation: {e}")
        return {
            "statusCode": 500,
            "body": json.dumps(f"Error invoking StartConversation: {str(e)}")
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
