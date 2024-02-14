from django.shortcuts import render
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from rest_framework.parsers import JSONParser
from rest_framework.views import APIView
from rest_framework.response import Response
from django.views.decorators.http import require_POST

from HybridRecommender.models import Tool, Cure, Disease, Symptom, Pest, CaringMethod, User, WeatherCondition, Location
from HybridRecommender.serializers import ToolSerializer, CureSerializer, DiseaseSerializerWrite, DiseaseSerializerRead,\
    SymptomSerializer, PestSerializerRead, PestSerializerWrite, CaringMethodSerializer, UserSerializerWrite, WeatherConditionSerializer

# Create your views here.

@csrf_exempt
def toolApi(request,id=0):
    if request.method=='GET':
        tools = Tool.objects.all()
        tools_serializer=ToolSerializer(tools, many=True)
        return JsonResponse(tools_serializer.data, safe=False)
    elif request.method=='POST':
        tool_data=JSONParser().parse(request)
        tool_serializer=ToolSerializer(data=tool_data)
        if tool_serializer.is_valid():
            tool_serializer.save()
            return JsonResponse("Added Successfully", safe=False)
        return JsonResponse("failed to Add", safe=False)
    elif request.method=='PUT':
        tool_data=JSONParser().parse(request)
        tool=Tool.objects.get(id=tool_data['id'])
        tool_serializer=ToolSerializer(tool,data=tool_data)
        if tool_serializer.is_valid():
            tool_serializer.save()
            return JsonResponse("Update Successfully", safe=False)
        return JsonResponse("Failed to Update")
    elif request.method=='DELETE':
        tool=Tool.objects.get(id=id)
        tool.delete()
        return JsonResponse("Deleted Successfully", safe=False)

@csrf_exempt
def cureApi(request,id=0):
    if request.method=='GET':
        cures = Cure.objects.all()
        cures_serializer=CureSerializer(cures, many=True)
        return JsonResponse(cures_serializer.data, safe=False)
    elif request.method=='POST':
        cure_data=JSONParser().parse(request)
        cure_serializer=CureSerializer(data=cure_data)
        print("Cureeeeeee")
        if cure_serializer.is_valid():
            cure_serializer.save()
            return JsonResponse("Added Successfully", safe=False)
        return JsonResponse("failed to Add", safe=False)
    elif request.method=='PUT':
        cure_data=JSONParser().parse(request)
        cure=Cure.objects.get(id=cure_data['id'])
        cure_serializer=CureSerializer(cure,data=cure_data)
        if cure_serializer.is_valid():
            cure_serializer.save()
            return JsonResponse("Update Successfully", safe=False)
        return JsonResponse("Failed to Update")
    elif request.method=='DELETE':
        cure=Cure.objects.get(id=id)
        cure.delete()
        return JsonResponse("Deleted Successfully", safe=False)

@csrf_exempt
def diseaseApi(request,id=0):
    if request.method=='GET':
        diseases = Disease.objects.all()
        diseases_serializer=DiseaseSerializerWrite(diseases, many=True)
        return JsonResponse(diseases_serializer.data, safe=False)
    elif request.method=='POST':
        disease_data=JSONParser().parse(request)
        disease_serializer=DiseaseSerializerWrite(data=disease_data)
        if disease_serializer.is_valid():
            disease_serializer.save()
            return JsonResponse("Added Successfully", safe=False)
        else:
            print(disease_serializer.errors)
            return JsonResponse("failed to Add", safe=False)
    elif request.method=='PUT':
        disease_data=JSONParser().parse(request)
        disease=Disease.objects.get(id=disease_data['id'])
        disease_serializer=DiseaseSerializerWrite(disease,data=disease_data)
        if disease_serializer.is_valid():
            disease_serializer.save()
            return JsonResponse("Update Successfully", safe=False)
        return JsonResponse("Failed to Update")
    elif request.method=='DELETE':
        disease=Disease.objects.get(id=id)
        disease.delete()
        return JsonResponse("Deleted Successfully", safe=False)

@csrf_exempt
def symptomApi(request,id=0):
    if request.method=='GET':
        symptoms = Symptom.objects.all()
        symptoms_serializer=SymptomSerializer(symptoms, many=True)
        return JsonResponse(symptoms_serializer.data, safe=False)
    elif request.method=='POST':
        symptom_data=JSONParser().parse(request)
        symptom_serializer=SymptomSerializer(data=symptom_data)
        if symptom_serializer.is_valid():
            symptom_serializer.save()
            return JsonResponse("Added Successfully", safe=False)
        return JsonResponse("failed to Add", safe=False)
    elif request.method=='PUT':
        symptom_data=JSONParser().parse(request)
        symptom=Symptom.objects.get(id=symptom_data['id'])
        symptom_serializer=SymptomSerializer(symptom,data=symptom_data)
        if symptom_serializer.is_valid():
            symptom_serializer.save()
            return JsonResponse("Update Successfully", safe=False)
        return JsonResponse("Failed to Update")
    elif request.method=='DELETE':
        symptom=Symptom.objects.get(id=id)
        symptom.delete()
        return JsonResponse("Deleted Successfully", safe=False)

@csrf_exempt
def pestApi_read(request, name=''):
    if request.method == 'GET':
        if not name:
            pests = Pest.objects.all()
        else:
            pests = Pest.objects.filter(name__iexact=name)
        pests_serializer = PestSerializerRead(pests, many=True)
        return JsonResponse(pests_serializer.data, safe=False)


def pestApi(request, pest_name=''):
    print(f"Received pest name: {pest_name}")
    if request.method == 'GET':
        if not pest_name:
            pests = Pest.objects.all()
        else:
            pests = Pest.objects.filter(name__iexact=pest_name)

        pests_serializer=PestSerializerRead(pests, many=True)
        serialized_data = pests_serializer.data
        print(f"Serialized Data: {serialized_data}")  # Add this line for debugging

        return JsonResponse(serialized_data, safe=False)
    elif request.method == 'POST':
        pest_data = JSONParser().parse(request)
        pest_serializer = PestSerializerWrite(data=pest_data)
        if pest_serializer.is_valid():
            pest_serializer.save()
            return JsonResponse("Added Successfully", safe=False)
        return JsonResponse("failed to Add", safe=False)
    elif request.method == 'PUT':
        pest_data = JSONParser().parse(request)
        pest = Pest.objects.get(id=pest_data['id'])
        pest_serializer = PestSerializerWrite(pest, data=pest_data)
        if pest_serializer.is_valid():
            pest_serializer.save()
            return JsonResponse("Update Successfully", safe=False)
        return JsonResponse("Failed to Update")
    elif request.method == 'DELETE':
        pest = Pest.objects.get(id=id)
        pest.delete()
        return JsonResponse("Deleted Successfully", safe=False)

@csrf_exempt
def caringMethodApi(request, id=0):
    if request.method == 'GET':
        methods = CaringMethod.objects.all()
        methods_serializer = CaringMethodSerializer(methods, many=True)
        return JsonResponse(methods_serializer.data, safe=False)
    elif request.method == 'POST':
        method_data = JSONParser().parse(request)
        method_serializer =CaringMethodSerializer(data=method_data)
        if method_serializer.is_valid():
            method_serializer.save()
            return JsonResponse("Added Successfully", safe=False)
        return JsonResponse("failed to Add", safe=False)
    elif request.method=='PUT':
        method_data=JSONParser().parse(request)
        method=CaringMethod.objects.get(id=method_data['id'])
        method_serializer=CaringMethodSerializer(method,data=method_data)
        if method_serializer.is_valid():
            method_serializer.save()
            return JsonResponse("Update Successfully", safe=False)
        return JsonResponse("Failed to Update")
    elif request.method=='DELETE':
        method=CaringMethod.objects.get(id=id)
        method.delete()
        return JsonResponse("Deleted Successfully", safe=False)

@csrf_exempt
@require_POST
def userApi(request):
    user_data=JSONParser().parse(request)
    email = user_data['email']
    location_name = user_data['location']
    location_id = Location.objects.get(name = location_name).id
    user_data['location'] = location_id
    users = User.objects.filter(email= email)
    if len(users)>0:
        user = User.objects.get(email = email)
        user_data['id'] = user.id
        user_serializer=UserSerializerWrite(user, data=user_data)
    else:
        user_serializer=UserSerializerWrite(data=user_data)
    if user_serializer.is_valid():
        user_serializer.save()
        return JsonResponse("Added Successfully", safe=False)
    return JsonResponse("failed to Add", safe=False)


@csrf_exempt
def weather_condition_api(request):
    if request.method == 'POST':
        weather_data = JSONParser().parse(request)
        weather_serializer =WeatherConditionSerializer(data = weather_data)
        if weather_serializer.is_valid():
            weather_serializer.save()
            return JsonResponse("Added Successfully", safe=False)
        return JsonResponse("failed to Add", safe=False)

    elif request.method == 'PUT':
        weather_data = JSONParser().parse(request)
        weather = WeatherCondition.objects.get(id=weather_data['id'])
        weather_serializer = WeatherConditionSerializer(weather, data=weather_data)
        if weather_serializer.is_valid():
            weather_serializer.save()
            return JsonResponse("Update Successfully", safe=False)
        return JsonResponse("Failed to Update")

@csrf_exempt
def diseaseApi_read(request, name=''):
    if request.method == 'GET':
        if not name:
            diseases = Disease.objects.all()
        else:
            diseases = Disease.objects.filter(name=name)
        diseases_serializer = DiseaseSerializerRead(diseases, many=True)
        return JsonResponse(diseases_serializer.data, safe=False)