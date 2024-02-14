import json

from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST, require_GET

from django.utils import timezone
from datetime import date, timedelta

from HybridRecommender.models import User, Location, Pest, LocationPest, UserPest
from HybridRecommender.serializers import UserPestsSerializer, LocationPestsSerializer
from HybridRecommender.data_preprocessing import preprocess_text

def is_less_than_a_week_ago(input_date):
    current_date = date.today()
    one_week_ago = current_date - timedelta(days=7)
    return input_date < one_week_ago


def get_user_pest_object(user_id, pest_id):
    pest = Pest.objects.get(id=pest_id)
    user_pests = UserPest.objects.filter(user=user_id)
    user_pests = [user_pest for user_pest in user_pests if user_pest.pest == pest]
    if len(user_pests) > 0:
        return True, user_pests[0]
    else:
        return False, 0


def get_location_pest_object(location_id, pest_id):
    pest = Pest.objects.get(id=pest_id)
    location_pests = LocationPest.objects.filter(location=location_id)
    location_pests = [location_pest for location_pest in location_pests if location_pest.location == location_id]
    if len(location_pests) > 0:
        return True, location_pests[0]
    else:
        return False, 0


def save_user_pest(user, pest, situation, entered_symptoms):
    data = {'user': user, 'pest': pest, 'date': timezone.now().date(), 'situation': situation, 'entered_symptoms': entered_symptoms}
    serializer = UserPestsSerializer(data=data)
    if serializer.is_valid():
        serializer.save()


def save_location_pest(location, pest, new_case):
    data = {'location': location, 'pest': pest, 'last_date': timezone.now().date(), 'cases': new_case}
    serializer = LocationPestsSerializer(data=data)
    if serializer.is_valid():
        serializer.save()


def send_to_people(location, pest, given_user_id):
    users_ids = [user.id for user in User.objects.filter(location=location).exclude(id=given_user_id)]
    for user_id in users_ids:
        object_exist, user_pest_object = get_user_pest_object(user_id, pest)
        if not object_exist:
            save_user_pest(user_id, pest, 1)


@csrf_exempt
@require_POST
def pest_user_api(request):
    data = json.loads(request.body.decode('utf-8'))
    user_email = data.get('user', '')
    pest_id = data.get('pest', 0)
    # entered_symptoms = data.get('symptoms', [])
    # entered_symptoms = [preprocess_text(entered_symptom) for entered_symptom in entered_symptoms]
    # entered_symptoms = entered_symptoms.join(" ")
    user = User.objects.get(email=user_email)
    user_id = user.id
    location = Location.objects.get(id=user.location.id)
    location_id = location.id
    object_exist, user_pest_object = get_user_pest_object(user_id, pest_id)
    new_inflect = True
    if object_exist:
        if user_pest_object.situation == 2:
            new_inflect = False
        else:
            user_pest_object.delete()

    if new_inflect:
        save_user_pest(user_id, pest_id, 2, []) #entered_symptoms)

        object_exist, location_pest_object = get_location_pest_object(location.id, pest_id)

        if object_exist:
            new_case = location_pest_object.cases + 1 if is_less_than_a_week_ago(location_pest_object.Last_date) else 1
            location_pest_object.delete()
            if new_case == 3:
                send_to_people(location_id, pest_id, user_id)
        else:
            new_case = 1
        save_location_pest(location_id, pest_id, new_case)

    return JsonResponse("Added Successfully", safe=False)


@csrf_exempt
@require_GET
def area_pests(request, location_name):
    location = Location.objects.get(name= location_name)
    locaton_pests = LocationPest.objects.filter(location = location)
    locaton_pests = [location_pest for location_pest in locaton_pests if location_pest.cases>=3]
    all_data = [{"pest_name":location_pest.pest.name, "cases": location_pest.cases, "pest_id": location_pest.pest.id} for location_pest in locaton_pests]
    return JsonResponse(all_data, safe=False)
