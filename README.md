
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
  "ProductId": {
    "S": "TravelAgency"
  },
  "Price": {
    "N": "1200"
  },
  "ProductDescription": {
    "S": "An all-inclusive travel package with accommodation, meals, and guided tours to scenic destinations."
  },
  "ProductLevels": {
    "M": {
      "Level1": {
        "M": {
          "Description": {
            "S": "Basic accommodation with essential amenities for budget-conscious travelers."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Neha supports brands with a positive social impact."
              },
              "Name": {
                "S": "Neha Banerjee"
              },
              "PrimaryTrait": {
                "S": "Socially conscious"
              }
            }
          }
        }
      },
      "Level2": {
        "M": {
          "Description": {
            "S": "Comfortable lodging with guided tours and some meal inclusions."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Ravi seeks the best deals."
              },
              "Name": {
                "S": "Ravi Patel"
              },
              "PrimaryTrait": {
                "S": "Price-sensitive"
              }
            }
          }
        }
      },
      "Level3": {
        "M": {
          "Description": {
            "S": "Premium travel package with better accommodation and more meal options."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Sara loves trends and frequently makes spontaneous purchases."
              },
              "Name": {
                "S": "Sara Verma"
              },
              "PrimaryTrait": {
                "S": "Impulse buyer"
              }
            }
          }
        }
      },
      "Level4": {
        "M": {
          "Description": {
            "S": "Luxury package with premium accommodation, all meals included, and exclusive tours."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Akshay thoroughly researches products before buying."
              },
              "Name": {
                "S": "Akshay Mehta"
              },
              "PrimaryTrait": {
                "S": "Research-driven"
              }
            }
          }
        }
      }
    }
  },
  "ProductName": {
    "S": "ExploreWorld Package"
  }
}





{
  "ProductId": {
    "S": "Smartphone"
  },
  "Price": {
    "N": "399"
  },
  "ProductDescription": {
    "S": "A smartphone with a 6.1-inch display, 128GB storage, and 4000mAh battery."
  },
  "ProductLevels": {
    "M": {
      "Level1": {
        "M": {
          "Description": {
            "S": "Basic features for everyday use at an affordable price."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Sara loves trends and frequently makes spontaneous purchases."
              },
              "Name": {
                "S": "Sara Verma"
              },
              "PrimaryTrait": {
                "S": "Impulse buyer"
              }
            }
          }
        }
      },
      "Level2": {
        "M": {
          "Description": {
            "S": "Improved features with a focus on battery life and camera quality."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Akshay thoroughly researches products before buying."
              },
              "Name": {
                "S": "Akshay Mehta"
              },
              "PrimaryTrait": {
                "S": "Research-driven"
              }
            }
          }
        }
      },
      "Level3": {
        "M": {
          "Description": {
            "S": "High-end specifications with better durability and brand support."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Neha prefers brands with a positive social impact."
              },
              "Name": {
                "S": "Neha Banerjee"
              },
              "PrimaryTrait": {
                "S": "Socially conscious"
              }
            }
          }
        }
      },
      "Level4": {
        "M": {
          "Description": {
            "S": "Top-tier specifications with exclusive features and premium customer support."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Ravi looks for the best deals and carefully compares prices."
              },
              "Name": {
                "S": "Ravi Patel"
              },
              "PrimaryTrait": {
                "S": "Price-sensitive"
              }
            }
          }
        }
      }
    }
  },
  "ProductName": {
    "S": "TechPlus X5"
  }
}






{
  "ProductId": {
    "S": "Sneakers"
  },
  "Price": {
    "N": "90"
  },
  "ProductDescription": {
    "S": "Durable and stylish sneakers with excellent arch support for daily wear."
  },
  "ProductLevels": {
    "M": {
      "Level1": {
        "M": {
          "Description": {
            "S": "Affordable, durable sneakers for occasional wear."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Ravi seeks the best deals."
              },
              "Name": {
                "S": "Ravi Patel"
              },
              "PrimaryTrait": {
                "S": "Price-sensitive"
              }
            }
          }
        }
      },
      "Level2": {
        "M": {
          "Description": {
            "S": "Enhanced comfort and support for regular use."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Sara loves trends and frequently makes spontaneous purchases."
              },
              "Name": {
                "S": "Sara Verma"
              },
              "PrimaryTrait": {
                "S": "Impulse buyer"
              }
            }
          }
        }
      },
      "Level3": {
        "M": {
          "Description": {
            "S": "High-quality materials for long-lasting daily use."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Akshay thoroughly researches products before buying."
              },
              "Name": {
                "S": "Akshay Mehta"
              },
              "PrimaryTrait": {
                "S": "Research-driven"
              }
            }
          }
        }
      },
      "Level4": {
        "M": {
          "Description": {
            "S": "Premium design with maximum durability and comfort."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Neha supports brands with a positive social impact."
              },
              "Name": {
                "S": "Neha Banerjee"
              },
              "PrimaryTrait": {
                "S": "Socially conscious"
              }
            }
          }
        }
      }
    }
  },
  "ProductName": {
    "S": "ComfortStride"
  }
}







{
  "ProductId": {
    "S": "FinancialAdvisor"
  },
  "Price": {
    "N": "40"
  },
  "ProductDescription": {
    "S": "A financial advisory service offering personalized investment and tax planning."
  },
  "ProductLevels": {
    "M": {
      "Level1": {
        "M": {
          "Description": {
            "S": "Basic financial advice for budgeting and saving."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Akshay thoroughly researches products before buying."
              },
              "Name": {
                "S": "Akshay Mehta"
              },
              "PrimaryTrait": {
                "S": "Research-driven"
              }
            }
          }
        }
      },
      "Level2": {
        "M": {
          "Description": {
            "S": "Comprehensive investment planning with quarterly check-ins."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Neha supports brands with a positive social impact."
              },
              "Name": {
                "S": "Neha Banerjee"
              },
              "PrimaryTrait": {
                "S": "Socially conscious"
              }
            }
          }
        }
      },
      "Level3": {
        "M": {
          "Description": {
            "S": "Advanced financial strategies with personalized tax planning."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Ravi seeks the best deals and compares prices carefully."
              },
              "Name": {
                "S": "Ravi Patel"
              },
              "PrimaryTrait": {
                "S": "Price-sensitive"
              }
            }
          }
        }
      },
      "Level4": {
        "M": {
          "Description": {
            "S": "Exclusive wealth management with priority support and tailored investment plans."
          },
          "Persona": {
            "M": {
              "Description": {
                "S": "Sara loves trends and frequently makes spontaneous purchases."
              },
              "Name": {
                "S": "Sara Verma"
              },
              "PrimaryTrait": {
                "S": "Impulse buyer"
              }
            }
          }
        }
      }
    }
  },
  "ProductName": {
    "S": "SmartInvest Advisory"
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
