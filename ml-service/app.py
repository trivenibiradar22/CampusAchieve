
from fastapi import FastAPI
from pydantic import BaseModel
import joblib
import numpy as np

app = FastAPI(title="Campus Achieve ML Service")

model = joblib.load("svm.joblib")


class PredictionRequest(BaseModel):
    certificate_name: str
    description: str


@app.get("/")
def home():
    return {"message": "Campus Achieve ML Service is running"}


@app.post("/predict")
def predict_category(request: PredictionRequest):

    text = (
        request.certificate_name + " " +
        request.description
    )

    prediction = model.predict([text])[0]

    scores = model.decision_function([text])[0]
    probabilities = np.exp(scores - np.max(scores))
    confidence = (
        probabilities / probabilities.sum()
    ).max() * 100

    return {
        "predicted_category": prediction,
        "confidence": round(float(confidence), 2)
    }
