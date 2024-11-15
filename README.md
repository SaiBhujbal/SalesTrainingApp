
# Negotiation AI Chat Application

This repository contains the code and setup details for a **Negotiation AI Chat Application**. The project includes an Android app built with Kotlin and a backend powered by AWS services. The app is designed to simulate negotiation scenarios where the user acts as a salesperson, and the AI plays the role of a customer.

---

## Features

- **Level-Based Negotiation**: AI personas with varying traits simulate different levels of customer interaction.
- **Conviction Scoring**: AI evaluates the user's negotiation skills with a conviction score.
- **Mood Analysis**: AI assesses the tone of conversations.
- **Dynamic Conversation**: Conversations are stored, retrieved, and continued seamlessly.
- **Progress Tracking**: Users can reset or continue conversations based on their progress.

---

## Tech Stack

### **Frontend**
- Kotlin (Android)

### **Backend**
- AWS Lambda
- AWS SageMaker
- Amazon DynamoDB
- AWS API Gateway

---

## AWS Resources

### **IAM Policies**
#### 1. **LambdaInvokeAllFunctionsPolicy**
Allows Lambda functions to invoke other functions.
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": "lambda:InvokeFunction",
            "Resource": "arn:aws:lambda:us-east-1:203918872714:function:*"
        }
    ]
}
```

#### 2. **SagemakerInvokeEndpointPolicy**
Allows Lambda functions to invoke SageMaker endpoints.
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "VisualEditor0",
            "Effect": "Allow",
            "Action": "sagemaker:InvokeEndpoint",
            "Resource": "*"
        }
    ]
}
```

---

### **DynamoDB Tables**
1. **ChatHistory**: Stores conversation history.
    - **Primary Key**: `session_id` (String)
    - **Sort Key**: `timestamp` (Number)
    - **Attributes**: `user_input`, `ai_response`

2. **Products**: Contains product and persona details.
    - **Primary Key**: `ProductId` (String)

3. **PersonaProgress**: Tracks user progress.
    - **Primary Key**: `UserId` (String)
    - **Sort Key**: `ProductId` (String)

---

### **Lambda Functions**
1. **analyze_sentiment**
   - Analyzes customer responses and provides:
     - Conviction score
     - Mood (Positive, Neutral, Skeptical, or Negative)
     - Convinced status (True/False)

2. **ContinueConversation**
   - Fetches ongoing conversations from the `ChatHistory` table.
   - Generates responses using SageMaker.

3. **StartConversation**
   - Starts a new conversation with the AI customer.
   - Generates a session ID and initializes chat in `ChatHistory`.

4. **check_progress**
   - Retrieves progress for a specific user and product from `PersonaProgress`.

5. **start_or_continue_conversation**
   - Handles logic for starting or continuing a conversation based on progress.

6. **reset_progress**
   - Resets user progress for a product to Level 1.

---

## How It Works

### **Conversation Flow**
1. **Start/Continue**:
   - `start_or_continue_conversation` determines whether to start fresh or continue based on the progress retrieved from `PersonaProgress`.

2. **Chat Progression**:
   - Conversations are stored in the `ChatHistory` table.
   - The AI generates responses using SageMaker.

3. **Conviction and Mood Analysis**:
   - At each step, the user's input is analyzed for conviction and mood using the `analyze_sentiment` function.

4. **Progress Tracking**:
   - Progress and levels are updated in `PersonaProgress`.

---

## Sample Data

### **Products Table**
```json
{
  "ProductId": "TravelAgency",
  "ProductName": "ExploreWorld Package",
  "Price": 1200,
  "ProductDescription": "An all-inclusive travel package with accommodation, meals, and guided tours to scenic destinations.",
  "ProductLevels": {
    "Level1": {
      "Description": "Basic accommodation with essential amenities for budget-conscious travelers.",
      "Persona": {
        "Name": "Neha Banerjee",
        "PrimaryTrait": "Socially conscious",
        "Description": "Neha supports brands with a positive social impact."
      }
    },
    ...
  }
}
```

---

## Setup Instructions

### **AWS Configuration**
1. **Create DynamoDB Tables**:
   - `ChatHistory`, `Products`, and `PersonaProgress` with schemas as described above.

2. **Setup SageMaker**:
   - Deploy a model (e.g., Llama 3) and get the endpoint name.

3. **Create Lambda Functions**:
   - Upload the provided Python scripts for each function.
   - Assign the respective IAM policies.

4. **Setup API Gateway**:
   - Link the Lambda functions to appropriate endpoints.

---

### **Android Application**
1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   ```

2. **Setup AWS SDK**:
   - Integrate AWS SDK for Kotlin.

3. **API Integration**:
   - Use API Gateway URLs for communication.

---

## Future Enhancements
- Add more products and levels with dynamic personas.
- Integrate advanced sentiment analysis models.
- Provide detailed analytics for user performance.

---

## License
This project is licensed under the MIT License. See `LICENSE` for details.

---

## Contact
For questions or support, please contact:

- **Name**: [Sai Bhujbal]
- **Email**: [sai.bhujbal.btech2022@sitpune.edu.in]
- **LinkedIn**: [https://www.linkedin.com/in/sai-bhujbal/]

---
