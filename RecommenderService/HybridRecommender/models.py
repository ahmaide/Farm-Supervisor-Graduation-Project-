from djongo import models


class Tool(models.Model):
    id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=60)

    class Meta:
        db_table = 'tool'


class Pest(models.Model):
    id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=40)
    symptoms = models.ManyToManyField('Symptom')
    pestType = models.CharField(max_length=20)
    pestInfo = models.CharField(max_length=80)
    cures = models.ManyToManyField('Cure')
    caringMethods = models.ManyToManyField('CaringMethod')
    startMonth = models.IntegerField()
    endMonth = models.IntegerField()

    class Meta:
        db_table = 'pest'


class Disease(models.Model):
    id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=40)
    causes = models.CharField(max_length=200)
    symptoms = models.ManyToManyField('Symptom')
    cures = models.ManyToManyField('Cure')
    caringMethods = models.ManyToManyField('CaringMethod')

    class Meta:
        db_table = 'disease'


class Cure(models.Model):
    id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=50)
    description = models.CharField(max_length=200)
    times = models.IntegerField()
    period = models.CharField(max_length=5)
    tools = models.ManyToManyField('Tool')

    class Meta:
        db_table = 'cure'


class Symptom(models.Model):
    id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=40)
    description = models.CharField(max_length=100)
    percentage = models.IntegerField()

    class Meta:
        db_table = 'symptom'


class CaringMethod(models.Model):
    id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=40)
    description = models.CharField(max_length=120)
    startMonth = models.IntegerField()
    endMonth = models.IntegerField()
    tools = models.ManyToManyField('Tool')

    class Meta:
        db_table = 'caringMethod'


class PlantType(models.Model):
    id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=40)
    description = models.CharField(max_length=80)

    class Meta:
        db_table = 'plantType'


class WeatherCondition(models.Model):
    id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=40)
    lowerTemp = models.IntegerField()
    upperTemp = models.IntegerField()
    caringMethods = models.ManyToManyField('CaringMethod')
    class Meta:
        db_table = 'weatherCondition'


class Location(models.Model):
    id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=40)
    avgHumidity = models.FloatField()
    avgHeight = models.FloatField()
    locationType = models.CharField(max_length=50)

    class Meta:
        db_table = 'location'


class User(models.Model):
    id = models.AutoField(primary_key=True)
    email = models.CharField(max_length=40)
    location = models.ForeignKey(Location, on_delete=models.CASCADE)
    caringMethods = models.ManyToManyField('CaringMethod')

    class Meta:
        db_table = 'user'


class UserDisease(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    disease = models.ForeignKey(Disease, on_delete=models.CASCADE)
    date = models.DateField()
    situation = models.IntegerField(default=0)
    entered_symptoms = models.CharField(max_length=500)
    class Meta:
        db_table = 'user_disease'


class UserPest(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    pest = models.ForeignKey(Pest, on_delete=models.CASCADE)
    date = models.DateField()
    situation = models.IntegerField(default=0)
    entered_symptoms = models.CharField(max_length=500)
    class Meta:
        db_table = 'user_pest'


class LocationDisease(models.Model):
    location = models.ForeignKey(Location, on_delete=models.CASCADE)
    disease = models.ForeignKey(Disease, on_delete=models.CASCADE)
    last_date = models.DateField()
    cases = models.IntegerField(default=0)

    class Meta:
        db_table = 'location_disease'


class LocationPest(models.Model):
    location = models.ForeignKey(Location, on_delete=models.CASCADE)
    pest = models.ForeignKey(Pest, on_delete=models.CASCADE)
    last_date = models.DateField()
    cases = models.IntegerField(default=0)

    class Meta:
        db_table = 'location_pest'
