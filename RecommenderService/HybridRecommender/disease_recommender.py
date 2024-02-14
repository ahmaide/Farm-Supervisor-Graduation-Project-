import json

import nltk
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST
from nltk.corpus import stopwords
from fuzzywuzzy import process

from HybridRecommender.models import Disease, Symptom
from HybridRecommender.serializers import DiseaseSerializerRead
from HybridRecommender.views import diseaseApi
from HybridRecommender.data_preprocessing import preprocess_text


class ProcessedDisease:
    def __init__(self, id):
        self.id = id
        self.symptoms = []

class DemoDisease:
    def __init__(self, id, name):
        self.id = id
        self.name = name


def preprocess_disease(disease: Disease):
    processed_diseasae = ProcessedDisease(disease.id)
    processed_diseasae.symptoms = [preprocess_text(symptom.description) for symptom in disease.symptoms.all()]
    return processed_diseasae


def disease_search(entered_name):
    disease_objects = Disease.objects.all()
    processed_diseases = [DemoDisease(disease.id, preprocess_text(disease.name)) for disease in disease_objects]
    disease_names = [disease.name for disease in processed_diseases]
    matches = process.extract(entered_name, disease_names, limit=5)
    matches = [match for match in matches if match[1] > 80]
    matches = [match[0] for match in matches]
    matches = [disease for disease in processed_diseases if disease.name in matches]
    matches = [disease for disease in disease_objects if any(disease.id == proDisease.id for proDisease in matches)]
    matched_diseases = [match.name for match in matches]
    return matched_diseases


def disease_recommender(symptoms):
    processed_symptoms = [preprocess_text(symptom) for symptom in symptoms]
    diseases = Disease.objects.all()
    processed_diseases = [preprocess_disease(disease) for disease in diseases]

    disease_scores = {disease: 0 for disease in processed_diseases}

    for disease in processed_diseases:
        for symptom in disease.symptoms:
            highest_match = process.extractOne(symptom, processed_symptoms)
            disease_scores[disease] += highest_match[1]

    sorted_diseases = sorted(disease_scores.items(), key=lambda x: x[1], reverse=True)[0:3]
    sorted_diseases = [disease for disease in diseases if any(disease.id == pro_pest[0].id for pro_pest in sorted_diseases)]
    # sorted_diseases = [disease.name for disease in sorted_diseases]
    # if len(sorted_diseases)>3:
    #     return sorted_diseases[0:3]
    # else:
    #     return sorted_diseases
    return sorted_diseases

@csrf_exempt
@require_POST
def disease_search_api(request):
    try:
        data = json.loads(request.body.decode('utf-8'))
        # Extracting data from the JSON
        text = data.get("text", '')
        valid_data = disease_search(text)
        return JsonResponse({'text': valid_data})

    except Exception as e:
        return JsonResponse({'error': str(e)}, status=400)

@csrf_exempt
@require_POST
def disease_recommender_api(request):
    try:
        data = json.loads(request.body.decode('utf-8'))
        symptoms = data.get('symptoms', [])
        symptoms = [symptom['symptoms'] for symptom in symptoms]
        expected_diseases = disease_recommender(symptoms)
        expected_names = [disease.name for disease in expected_diseases]
        print(expected_diseases)
        diseases_serializer = DiseaseSerializerRead(expected_diseases, many = True)
        #return JsonResponse(diseases_serializer.data, safe=False)
        return JsonResponse({'diseases': expected_names})

    except Exception as e:
        return JsonResponse({'error': str(e)}, status=400)
