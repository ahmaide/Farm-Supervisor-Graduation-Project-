import json
import os

from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST, require_GET
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score

from models import UserDisease, Disease
from serializers import DiseaseSerializerRead
import joblib
all_data = UserDisease.objects.filter(situation=2)
from HybridRecommender.data_preprocessing import preprocess_text


filename = 'collabrative_models/diseases_model.pkl'


@csrf_exempt
@require_POST
def rebuild_disease_model(request):
    diseases_ids = [instance.disease.id for instance in all_data]
    entered_symptoms = [instance.entered_symptoms for instance in all_data]
    tfidf_vectorizer = TfidfVectorizer(max_features=5000)
    X = tfidf_vectorizer.fit_transform(entered_symptoms)
    y = diseases_ids

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    rf_model = RandomForestClassifier(n_estimators=100, random_state=42)
    rf_model.fit(X_train, y_train)

    y_pred = rf_model.predict(X_test)
    accuracy = accuracy_score(y_test, y_pred)
    result = {
        "accuracy": accuracy
    }
    joblib.dump(rf_model, filename)
    return JsonResponse(result)

@csrf_exempt
@require_GET
def disease_collabrative_recommender(request):
    data = json.loads(request.body.decode('utf-8'))
    symptoms = data.get('symptoms', [])
    symptoms = [preprocess_text(entered_symptom) for entered_symptom in symptoms]
    symptoms = symptoms.join(", ")
    symptoms = preprocess_text(symptoms)
    if os.path.exists(filename):
        rf_model = joblib.load(filename)
    else:
        rebuild_disease_model(None)
        rf_model = joblib.load(filename)
    symptoms_vectorized = TfidfVectorizer.transform([symptoms])
    predicted_disease_id = rf_model.predict(symptoms_vectorized)[0]
    disease = Disease.objects.get(predicted_disease_id)
    disease_serializer = DiseaseSerializerRead(disease, many=True)
    return JsonResponse(disease_serializer.data, safe=False)



