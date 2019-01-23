from django.urls import path

from . import views

urlpatterns = [
    path('', views.usage, name='backoffice_usage'),

]