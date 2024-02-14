from rest_framework import serializers
from .models import Tool, Pest, Disease, Cure, Symptom, CaringMethod, PlantType, WeatherCondition, Location, User
from .models import LocationPest, UserPest, LocationDisease, UserDisease

class ToolSerializer(serializers.ModelSerializer):
    class Meta:
        model = Tool
        fields = '__all__'

class SymptomSerializer(serializers.ModelSerializer):
    class Meta:
        model = Symptom
        fields = '__all__'

class CureSerializer(serializers.ModelSerializer):
    tools = serializers.PrimaryKeyRelatedField(
        many=True,
        queryset=Tool.objects.all()
    )

    class Meta:
        model = Cure
        fields = '__all__'


class CaringMethodSerializer(serializers.ModelSerializer):
    tools = serializers.PrimaryKeyRelatedField(
        many=True,
        queryset=Tool.objects.all()
    )

    class Meta:
        model = CaringMethod
        fields = '__all__'

class CaringMethodSerializerRead(serializers.ModelSerializer):
    tools = serializers.SerializerMethodField()

    class Meta:
        model = CaringMethod
        fields = '__all__'

    def get_tools(self, obj):
        return ToolSerializer(obj.tools.all(), many=True).data


class PlantTypeSerializer(serializers.ModelSerializer):
    class Meta:
        model = PlantType
        fields = '__all__'

class WeatherConditionSerializer(serializers.ModelSerializer):
    caringMethods = serializers.PrimaryKeyRelatedField(
        many=True,
        queryset=CaringMethod.objects.all(),
    )

    class Meta:
        model = WeatherCondition
        fields = '__all__'




class LocationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Location
        fields = '__all__'

class DiseaseSerializerWrite(serializers.ModelSerializer):
    symptoms = serializers.PrimaryKeyRelatedField(
        many=True,
        queryset=Symptom.objects.all()
    )
    cures = serializers.PrimaryKeyRelatedField(
        many=True,
        queryset=Cure.objects.all()
    )
    caringMethods = serializers.PrimaryKeyRelatedField(
        many=True,
        queryset=CaringMethod.objects.all(),
    )

    class Meta:
        model = Disease
        fields = '__all__'

class DiseaseSerializerRead(serializers.ModelSerializer):
    symptoms = serializers.SerializerMethodField()
    cures = serializers.SerializerMethodField()
    caringMethods = serializers.SerializerMethodField()

    class Meta:
        model = Disease
        fields = '__all__'

    def get_symptoms(self, obj):
        return SymptomSerializer(obj.symptoms.all(), many=True).data

    def get_cures(self, obj):
        return CureSerializer(obj.cures.all(), many=True).data

    def get_caringMethods(self, obj):
        return CaringMethodSerializer(obj.caringMethods.all(), many=True).data


class PestSerializerWrite(serializers.ModelSerializer):
    symptoms = SymptomSerializer(many=True)
    cures = CureSerializer(many=True)
    caringMethods = CaringMethodSerializer(many=True)

    class Meta:
        model = Pest
        fields = '__all__'


class PestSerializerRead(serializers.ModelSerializer):
    symptoms = serializers.SerializerMethodField()
    cures = serializers.SerializerMethodField()
    caringMethods = serializers.SerializerMethodField()

    class Meta:
        model = Pest
        fields = '__all__'

    def get_symptoms(self, obj):
        return SymptomSerializer(obj.symptoms.all(), many=True).data

    def get_cures(self, obj):
        return CureSerializer(obj.cures.all(), many=True).data

    def get_caringMethods(self, obj):
        return CaringMethodSerializer(obj.caringMethods.all(), many=True).data


class UserSerializerWrite(serializers.ModelSerializer):
    location = serializers.PrimaryKeyRelatedField(queryset=Location.objects.all())

    class Meta:
        model = User
        fields = ['id', 'email', 'location']


class LocationPestsSerializer(serializers.ModelSerializer):
    location = serializers.PrimaryKeyRelatedField(queryset=Location.objects.all())
    pest = serializers.PrimaryKeyRelatedField(queryset=Pest.objects.all())
    class Meta:
        model = LocationPest
        fields = ['location', 'pest', 'last_date', 'cases']

class UserPestsSerializer(serializers.ModelSerializer):
    user = serializers.PrimaryKeyRelatedField(queryset=User.objects.all())
    pest = serializers.PrimaryKeyRelatedField(queryset=Pest.objects.all())
    class Meta:
        model = UserPest
        fields = ['user', 'pest', 'date', 'situation', 'entered_symptoms']


class LocationDiseasesSerializer(serializers.ModelSerializer):
    location = serializers.PrimaryKeyRelatedField(queryset=Location.objects.all())
    disease = serializers.PrimaryKeyRelatedField(queryset=Disease.objects.all())
    class Meta:
        model = LocationDisease
        fields = ['location', 'disease', 'last_date', 'cases']

class UserDiseasesSerializer(serializers.ModelSerializer):
    user = serializers.PrimaryKeyRelatedField(queryset=User.objects.all())
    disease = serializers.PrimaryKeyRelatedField(queryset=Disease.objects.all())
    class Meta:
        model = UserDisease
        fields = ['user', 'disease', 'date', 'situation', 'entered_symptoms']
