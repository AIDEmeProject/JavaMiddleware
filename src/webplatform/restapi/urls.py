
from django.conf.urls import url, include
from django.urls import path

from rest_framework import routers,  viewsets

from restapi import views
from .serializers import *

# ViewSets define the view behavior.
class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer

# Routers provide an easy way of automatically determining the URL conf.
router = routers.DefaultRouter()
router.register(r'users', UserViewSet)


urlpatterns = [

    path('session/new', views.NewSession.as_view()),
    path('session/<str:token>/options', views.SessionOptions.as_view()),
    path('session/<str:token>/new-label', views.NewLabel.as_view()),
    path('session/<str:token>/label-whole-dataset', views.LabelWholeDataset.as_view()),
    url(r'^', include(router.urls)),
    url(r'^api-auth/', include('rest_framework.urls', namespace='rest_framework'))
]