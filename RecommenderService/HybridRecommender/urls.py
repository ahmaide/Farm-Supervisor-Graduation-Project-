from django.urls import path
from django.views.decorators.csrf import csrf_exempt
from .views import toolApi, pestApi, symptomApi, caringMethodApi, cureApi, userApi, diseaseApi, weather_condition_api,\
    pestApi_read, diseaseApi_read
from .pestRecommender import pest_search_api, pest_api_with_type, pest_api_without_type, get_pest_names, get_pest_info
from .pest_warnings import pest_user_api, area_pests
from .disease_recommender import disease_recommender_api, disease_search_api
from .disease_warning import disease_user_api
from .weather_warnings import weather_warning_api

urlpatterns = [
    path('pest_warning/', csrf_exempt(pest_user_api), name = 'pest_warning'),
    path('disease_warning/', csrf_exempt(disease_user_api), name = 'disease_warning'),
    path('user/', csrf_exempt(userApi), name = 'user_api'),
    path('tool/', csrf_exempt(toolApi), name='tool_api'),
    path('pest/<str:pest_name>/', csrf_exempt(pestApi), name='pest_api'),
    path('pests/<str:pest_name>/', csrf_exempt(get_pest_info), name='get_pest_info'),
    path('get_pest_names/', csrf_exempt(get_pest_names), name='get_pest_names'),
    path('disease/', csrf_exempt(diseaseApi), name='disease_api'),
    path('pest/<int:id>/', csrf_exempt(pestApi), name='pest_api'),
    path('symptom/', csrf_exempt(symptomApi), name='symptom_api'),
    path('caringMethod/', csrf_exempt(caringMethodApi), name='method_api'),
    path('cure/', csrf_exempt(cureApi), name='cure_api'),
    path('pestRecommender1/', csrf_exempt(pest_api_with_type), name='pest-recommender'),
    path('pestRecommender2/', csrf_exempt(pest_api_without_type), name='pest-recommender'),
    path('pestSearch/', csrf_exempt(pest_search_api), name='pest_search'),
    path('diseaseSearch/', csrf_exempt(disease_search_api), name='disease_search'),
    path('diseaseRecommender/', csrf_exempt(disease_recommender_api), name='disease-recommender'),
    path('weatherCondition/', csrf_exempt(weather_condition_api), name = 'weather_api'),
    path('weatherWarning/<int:id>/', csrf_exempt(weather_warning_api), name='weather_warning_api'),
    path('pestApiRead/<str:name>/', csrf_exempt(pestApi_read), name='pest_api_read'),
    path('diseaseApiRead/<str:name>/', csrf_exempt(diseaseApi_read), name='disease_api_read'),
    path('location_pests/<str:location_name>/', csrf_exempt(area_pests), name='location_pest')
]