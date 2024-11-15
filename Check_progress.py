import boto3
import json
from decimal import Decimal

# Initialize AWS services
dynamodb = boto3.resource('dynamodb')
persona_progress_table = dynamodb.Table('PersonaProgress')

# Helper function to convert Decimal values to JSON-compatible types
def decimal_to_float(obj):
    if isinstance(obj, Decimal):
        return float(obj)
    elif isinstance(obj, list):
        return [decimal_to_float(i) for i in obj]
    elif isinstance(obj, dict):
        return {k: decimal_to_float(v) for k, v in obj.items()}
    return obj

def lambda_handler(event, context):
    # Parse 'body' for both API Gateway and direct Lambda invocation scenarios
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

    # Extract user_id and product_id from the parsed body
    user_id = body.get("user_id")
    product_id = body.get("product_id")

    # Validate that required fields are present
    if not user_id or not product_id:
        return {
            "statusCode": 400,
            "body": json.dumps("Error: 'user_id' and 'product_id' are required.")
        }

    try:
        # Fetch progress details from DynamoDB
        progress_response = persona_progress_table.get_item(
            Key={'UserId': user_id, 'ProductId': product_id}
        )
        
        # Check if progress exists
        if 'Item' in progress_response:
            # Convert any Decimal types in the response to float
            levels_passed = decimal_to_float(progress_response['Item'].get('LevelsPassed', []))
            progress_percentage = decimal_to_float(progress_response['Item'].get('ProgressPercentage', 0))
            return {
                "statusCode": 200,
                "body": json.dumps({
                    "levels_passed": levels_passed,
                    "progress_percentage": progress_percentage
                })
            }
        else:
            # No progress found, return default values
            return {
                "statusCode": 200,
                "body": json.dumps({
                    "levels_passed": [],
                    "progress_percentage": 0
                })
            }
    except Exception as e:
        print(f"Error checking progress: {e}")
        return {
            "statusCode": 500,
            "body": json.dumps(f"Error checking progress: {str(e)}")
        }
