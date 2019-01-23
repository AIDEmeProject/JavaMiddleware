from django.contrib.auth.decorators import login_required
from django.shortcuts import render

from restapi.models import Session


@login_required()
def usage(request):

    sessions = Session.objects.all()

    return render(request, 'usage.html', {'sessions': sessions})


