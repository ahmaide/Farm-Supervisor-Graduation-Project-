import json
import urllib

import nltk
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST, require_GET
from nltk.corpus import stopwords
from fuzzywuzzy import process
import datetime

from HybridRecommender.models import Pest, Symptom
from HybridRecommender.serializers import PestSerializerRead
from HybridRecommender.views import pestApi
from HybridRecommender.data_preprocessing import preprocess_text


class ProcessedPest:
    def __init__(self, id, start_month, end_month):
        self.id = id
        self.pest_info = []
        self.symptoms = []
        self.start_month = start_month
        self.end_month = end_month


class DemoPest:
    def __init__(self, id, name):
        self.id = id
        self.name = name


def get_current_month():
    current_date = datetime.date.today()
    return current_date.month


def preprocess_pest(pest: Pest):
    processed_pest = ProcessedPest(pest.id, pest.startMonth, pest.endMonth)
    processed_pest.pest_info = [preprocess_text(info) for info in pest.pestInfo.split(',')]
    processed_pest.symptoms = [preprocess_text(symptom.description) for symptom in pest.symptoms.all()]
    return processed_pest


def pest_search(entered_name):
    pest_objects = Pest.objects.all()
    processed_pests = [DemoPest(pest.id, preprocess_text(pest.name)) for pest in pest_objects]
    pest_names = [pest.name for pest in processed_pests]
    matches = process.extract(entered_name, pest_names, limit=5)
    matches = [match for match in matches if match[1] > 80]
    matches = [match[0] for match in matches]
    matches = [pest for pest in processed_pests if pest.name in matches]
    matches = [pest for pest in pest_objects if any(pest.id == proPest.id for proPest in matches)]
    matched_pests = [match.name for match in matches]
    return matched_pests


def pest_recommend_with_type(pest_type, month, info, symptoms):
    processed_info = [preprocess_text(one_info) for one_info in info]
    processed_symptoms = [preprocess_text(symptom) for symptom in symptoms]
    pests = Pest.objects.filter(pestType=pest_type)
    processed_pests = [preprocess_pest(pest) for pest in pests]

    pest_scores = {pest: 0 for pest in processed_pests}

    for pest in processed_pests:

        processed_month_range = (pest.end_month - pest.start_month) % 12
        processed_current_month = (month - pest.start_month) % 12
        if processed_current_month <= processed_month_range:
            pest_scores[pest] += 10

        for symptom in pest.symptoms:
            highest_match = process.extractOne(symptom, processed_symptoms)
            pest_scores[pest] += highest_match[1]

        for one_info in pest.pest_info:
            highest_match = process.extractOne(one_info, processed_info)
            pest_scores[pest] += highest_match[1]

    sorted_pests = sorted(pest_scores.items(), key=lambda x: x[1], reverse=True)
    if len(sorted_pests) > 3:
        sorted_pests = sorted(pest_scores.items(), key=lambda x: x[1], reverse=True)[0:3]
    sorted_pests = [pest for pest in pests if any(pest.id == pro_pest[0].id for pro_pest in sorted_pests)]
    #sorted_pests = [pest.name for pest in sorted_pests]
    return sorted_pests


def pest_recommend_without_type(month, symptoms):
    processed_symptoms = [preprocess_text(symptom) for symptom in symptoms]
    pests = Pest.objects.all()
    processed_pests = [preprocess_pest(pest) for pest in pests]

    pest_scores = {pest: 0 for pest in processed_pests}

    for pest in processed_pests:

        processed_month_range = (pest.end_month - pest.start_month) % 12
        processed_current_month = (month - pest.start_month) % 12
        if processed_current_month <= processed_month_range:
            pest_scores[pest] += 10

        for symptom in pest.symptoms:
            highest_match = process.extractOne(symptom, processed_symptoms)
            pest_scores[pest] += highest_match[1]

    sorted_pests = sorted(pest_scores.items(), key=lambda x: x[1], reverse=True)[0:3]
    sorted_pests = [pest for pest in pests if any(pest.id == pro_pest[0].id for pro_pest in sorted_pests)]
    #sorted_pests = [pest.name for pest in sorted_pests]
    # if len(sorted_pests) > 3:
    #     return sorted_pests[0:3]
    # else:
    #     return sorted_pests
    return sorted_pests


def get_pest_info(request, pest_name=''):
    print(f"Received pest name: {pest_name}")
    if request.method == 'GET':
        pest_name = urllib.parse.unquote(pest_name)
        print(pest_name)
        if not pest_name:
            pests = Pest.objects.all()
        else:
            pests = Pest.objects.filter(name__iexact=pest_name)

        pests_serializer = PestSerializerRead(pests, many=True)
        serialized_data = pests_serializer.data
        print(f"{serialized_data}")  # Add this line for debugging

        return JsonResponse({'pest_info': serialized_data[0]}, safe=False)


@require_GET
def get_pest_names(request):
    try:
        pest_objects = Pest.objects.all()
        print(f"pest_objects: {pest_objects}")

        pest_names = [pest.name for pest in pest_objects]
        print(f"pest_names: {pest_names}")

        return JsonResponse({'pest_names': pest_names})

    except Exception as e:
        print(f"Error: {e}")
        return JsonResponse({'error': str(e)}, status=400)

@csrf_exempt
@require_POST
def pest_search_api(request):
    try:
        data = json.loads(request.body.decode('utf-8'))
        # Extracting data from the JSON
        text = data.get("text", '')
        valid_data = pest_search(text)
        return JsonResponse({'text': valid_data})

    except Exception as e:
        return JsonResponse({'error': str(e)}, status=400)


@csrf_exempt
@require_POST
def pest_api_with_type(request):
    try:
        data = json.loads(request.body.decode('utf-8'))
        pest_type = data.get('type', [])
        pest_type = pest_type[0]['type']
        month = get_current_month()
        pest_info = data.get('pestInfo', [])
        pest_info = [info['pestInfo'] for info in pest_info]
        symptoms = data.get('symptoms', [])
        symptoms = [symptom['symptoms'] for symptom in symptoms]
        expected_pests = pest_recommend_with_type(pest_type, month, pest_info, symptoms)
        expected_names = [pest.name for pest in expected_pests]
        print(expected_names)
        pests_serializer = PestSerializerRead(expected_pests, many=True)
        #return JsonResponse(pests_serializer.data, safe=False)
        return JsonResponse({'pests': expected_names})

    except Exception as e:
        return JsonResponse({'error': str(e)}, status=400)


@csrf_exempt
@require_POST
def pest_api_without_type(request):
    try:
        data = json.loads(request.body.decode('utf-8'))
        month = get_current_month()
        symptoms = data.get('symptoms', [])
        symptoms = [symptom['symptoms'] for symptom in symptoms]
        expected_pests = pest_recommend_without_type(month, symptoms)
        expected_names = [pest.name for pest in expected_pests]
        print(expected_names)
        pests_serializer = PestSerializerRead(expected_pests, many=True)
        #return JsonResponse(pests_serializer.data, safe=False)
        return JsonResponse({'pests': expected_names})

    except Exception as e:
        return JsonResponse({'error': str(e)}, status=400)