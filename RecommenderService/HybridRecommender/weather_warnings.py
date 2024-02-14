import json
import urllib
import random

from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from .models import WeatherCondition, CaringMethod
from django.views.decorators.http import require_POST, require_GET
from .serializers import CaringMethodSerializerRead

def most_extreme_weather(weather, temp):
    weather_objects = WeatherCondition.objects.all()
    most_extreme = 0
    found_ones = []
    for weather_object in weather_objects:
        if weather in weather_object.name:
            found_ones.append(weather_object)
    if len(found_ones) > 1 and temp >= found_ones[1].lowerTemp and temp <= found_ones[1].upperTemp:
        found_one = found_ones[1]
    else:
        found_one = found_ones[0]
    if found_one.id > most_extreme:
        most_extreme = found_one.id
    return  most_extreme

@csrf_exempt
@require_GET
def weather_warning_api(request, id=0):
    print(id)
    weather = WeatherCondition.objects.get(id = id)
    print("Most Extreme: " + weather.name)
    random_numbers = random.sample(range(0, len(weather.caringMethods.all())), 4)
    caring_methods = [caring_method for i, caring_method in enumerate(weather.caringMethods.all())
                      if i in random_numbers]
    caring_method_serializer = CaringMethodSerializerRead(caring_methods, many=True)
    caring_methods_data = caring_method_serializer.data
    return JsonResponse(caring_methods_data, safe=False)