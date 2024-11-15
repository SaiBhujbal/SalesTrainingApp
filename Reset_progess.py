import boto3
import json

# Initialize AWS services
dynamodb = boto3.resource('dynamodb')
persona_progress_table = dynamodb.Table('PersonaProgress')

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
        # Delete the existing progress for the specified product
        response = persona_progress_table.delete_item(
            Key={'UserId': user_id, 'ProductId': product_id}
        )
        
        # Check the response for any errors or conditions
        if response.get('ResponseMetadata', {}).get('HTTPStatusCode') == 200:
            return {
                "statusCode": 200,
                "body": json.dumps("Progress reset to Level 1")
            }
        else:
            print(f"Unexpected response from DynamoDB: {response}")
            return {
                "statusCode": 500,
                "body": json.dumps("Error resetting progress: unexpected response from DynamoDB")
            }

    except Exception as e:
        print(f"Error resetting progress: {e}")
        return {
            "statusCode": 500,
            "body": json.dumps(f"Error resetting progress: {str(e)}")
        }
