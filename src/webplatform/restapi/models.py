from django.db import models

# Create your models here.
from user.models import Profile


from uuid import uuid4


def generate_token():
    return str(uuid4())

class Session(models.Model):

    token = models.CharField(max_length=100)
    column_number = models.IntegerField(null=True)
    row_number = models.IntegerField(null=True)
    user = models.ForeignKey(Profile, on_delete="cascade", null=True)


    classifier = models.CharField(null=True, max_length=50)
    learner = models.CharField(null=True, max_length=50)

    number_of_labeled_points = models.IntegerField(default=0)

    has_tsm = models.BooleanField(default=False)
    number_of_variable_groups = models.IntegerField(null=True)
    tsm_bound = models.FloatField(null=True)

    clicked_on_label_dataset = models.BooleanField(default=False)

    number_of_labeled_points = models.IntegerField(default=0)
    #was_satisfied = models.BooleanField()



    def __str__(self):
        return self.token

    @classmethod
    def create(cls, user):
        token = generate_token()
        return cls(token = token, user=user)

    def newSession(self):
        session = Session()
        session.token