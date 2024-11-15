import boto3
import json
import re

# Initialize AWS services
dynamodb = boto3.resource('dynamodb')
sagemaker_runtime = boto3.client('sagemaker-runtime')

# Define the SageMaker endpoint
ENDPOINT_NAME = 'jumpstart-dft-hf-llm-nvidia-llama3-20241115-072526'  # Replace with your actual endpoint name

def lambda_handler(event, context):
    ai_response = event.get("ai_response", "")
    session_id = event.get("session_id", "")

    if not ai_response or not session_id:
        return {
            "statusCode": 400,
            "body": "Error: 'ai_response' and 'session_id' are required."
        }

    # Construct a refined prompt with detailed instructions before the "Assistant:" label
    prompt_message = (
        f"System: You are an AI assistant responsible for analyzing a customer's response and evaluating their interest level in a product.\n\n"
        f"Instructions:\n"
        f"1. Provide a **Conviction Score** between 0 and 100, indicating the likelihood that the customer intends to purchase. Use these ranges:\n"
        f"   - 0-30: Minimal interest\n"
        f"   - 31-60: Mild interest with questions\n"
        f"   - 61-80: High interest, enthusiasm shown\n"
        f"   - 81-100: Very high interest, close to purchase\n"
        f"2. Provide the **Mood** as Positive, Neutral, Skeptical, or Negative, based on the customer's tone.\n"
        f"3. Set **Convinced Status** to True only if the customer explicitly signals readiness to buy.\n\n"
        f"Response Structure:\n"
        f"The response should be formatted exactly as follows:\n"
        f"Conviction Score: <value>\n"
        f"Mood: <mood>\n"
        f"Convinced: <True/False>\n\n"
        f"Customer Response:\n\"{ai_response}\"\n\n"
        f"Assistant:"
    )

    payload = {
        "inputs": prompt_message,
        "parameters": {
            "max_new_tokens": 50,
            "temperature": 0.5,
            "top_p": 0.9,
            "stop": ["\n"]
        }
    }

    try:
        # Invoke the SageMaker endpoint
        response = sagemaker_runtime.invoke_endpoint(
            EndpointName=ENDPOINT_NAME,
            ContentType="application/json",
            Body=json.dumps(payload)
        )
        response_body = response['Body'].read().decode('utf-8')
        response_payload = json.loads(response_body)
        analysis_result = response_payload[0].get('generated_text', '') if isinstance(response_payload, list) else response_payload.get('generated_text', '')

        # Attempt to extract conviction score, mood, and convinced status from the response
        conviction_score_match = re.search(r"conviction score: (\d+)", analysis_result, re.IGNORECASE)
        conviction_score = int(conviction_score_match.group(1)) if conviction_score_match else 15  # Default to minimal interest

        mood_match = re.search(r"mood: (positive|neutral|skeptical|negative)", analysis_result, re.IGNORECASE)
        mood = mood_match.group(1).capitalize() if mood_match else "Neutral"
        
        convinced_match = re.search(r"convinced: (true|false)", analysis_result, re.IGNORECASE)
        convinced = convinced_match.group(1).lower() == "true" if convinced_match else conviction_score >= 81

        return {
            "statusCode": 200,
            "body": json.dumps({
                "ai_response": ai_response,
                "conviction_score": conviction_score,
                "mood": mood,
                "convinced": convinced
            })
        }
    except Exception as e:
        print(f"Error processing sentiment analysis: {e}")
        return {
            "statusCode": 500,
            "body": f"Error processing sentiment analysis: {str(e)}"
        }
