import json

from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST

from django.utils import timezone
from datetime import date, timedelta

from HybridRecommender.models import User, Location, Disease, LocationDisease, UserDisease
from HybridRecommender.serializers import UserDiseasesSerializer, LocationDiseasesSerializer
from HybridRecommender.data_preprocessing import preprocess_text

def is_less_than_a_week_ago(input_date):
    current_date = date.today()
    one_week_ago = current_date - timedelta(days=7)
    return input_date < one_week_ago


def get_user_disease_object(user_id, disease_id):
    disease = Disease.objects.get(id=disease_id)
    user_diseases = UserDisease.objects.filter(user=user_id)
    user_diseases = [user_disease for user_disease in user_diseases if user_disease.disease == disease]
    if len(user_diseases) > 0:
        return True, user_diseases[0]
    else:
        return False, 0


def get_location_disease_object(location_id, disease_id):
    disease = Disease.objects.get(id=disease_id)
    location_diseases = LocationDisease.objects.filter(location=location_id)
    location_diseases = [location_disease for location_disease in location_diseases if location_disease.location == location_id]
    if len(location_diseases) > 0:
        return True, location_diseases[0]
    else:
        return False, 0


def save_user_disease(user, disease, situation, entered_symptoms):
    data = {'user': user, 'disease': disease, 'date': timezone.now().date(), 'situation': situation, 'entered_symptoms': entered_symptoms}
    serializer = UserDiseasesSerializer(data=data)
    if serializer.is_valid():
        serializer.save()


def save_location_disease(location, disease, new_case):
    data = {'location': location, 'disease': disease, 'last_date': timezone.now().date(), 'cases': new_case}
    serializer = LocationDiseasesSerializer(data=data)
    if serializer.is_valid():
        serializer.save()


def send_to_people(location, disease, given_user_id):
    users_ids = [user.id for user in User.objects.filter(location=location).exclude(id=given_user_id)]
    for user_id in users_ids:
        object_exist, user_disease_object = get_user_disease_object(user_id, disease)
        if not object_exist:
            save_user_disease(user_id, disease, 1)


@csrf_exempt
@require_POST
def disease_user_api(request):
    data = json.loads(request.body.decode('utf-8'))
    user_id = data.get('user', 0)
    disease_id = data.get('disease', 0)
    entered_symptoms = data.get('symptoms', [])
    entered_symptoms = [preprocess_text(entered_symptom) for entered_symptom in entered_symptoms]
    entered_symptoms = entered_symptoms.join(" ")
    user = User.objects.get(id=user_id)
    location = Location.objects.get(id=user.location.id)
    location_id = location.id
    object_exist, user_disease_object = get_user_disease_object(user_id, disease_id)
    new_inflect = True
    if object_exist:
        if user_disease_object.situation == 2:
            new_inflect = False
        else:
            user_disease_object.delete()

    if new_inflect:
        save_user_disease(user_id, disease_id, 2, entered_symptoms)

        object_exist, location_disease_object = get_location_disease_object(location.id, disease_id)

        if object_exist:
            new_case = location_disease_object.cases + 1 if is_less_than_a_week_ago(location_disease_object.Last_date) else 1
            location_disease_object.delete()
            if new_case == 3:
                send_to_people(location_id, disease_id, user_id)
        else:
            new_case = 1
        save_location_disease(location_id, disease_id, new_case)

    return JsonResponse("Added Successfully", safe=False)
