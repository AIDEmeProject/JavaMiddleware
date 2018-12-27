from django.urls import path

from . import views

urlpatterns = [
    path('', views.get_aide_plus, name='get_aide_plus'),
    path('download', views.download_software, name='download'),
]