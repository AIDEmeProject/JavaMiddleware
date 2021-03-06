import os
from django.http import HttpResponse
from django.shortcuts import render

from django.contrib.auth.decorators import login_required
from rest_framework.authtoken.models import Token


@login_required()
def get_aide_plus(request):
    user = request.user
    token, _ = Token.objects.get_or_create(user=user)

    return render(request, 'download.html', {'token':token})


@login_required()
def download_software(request):

    file_path = "./bin/aideplus.zip"

    with open(file_path, 'rb') as fh:
        response = HttpResponse(fh.read(), content_type="application/zip")
        response['Content-Disposition'] = 'inline; filename=' + os.path.basename(file_path)
        return response
