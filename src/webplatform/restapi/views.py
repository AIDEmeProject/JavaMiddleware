from django.http import Http404
from django.shortcuts import render

# Create your views here.
from rest_framework import status, permissions
from rest_framework.authentication import TokenAuthentication
from rest_framework.authtoken.models import Token
from rest_framework.decorators import permission_classes, authentication_classes
from rest_framework.fields import ReadOnlyField
from rest_framework.relations import PrimaryKeyRelatedField
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework import serializers
from restapi.models import Session


@authentication_classes((TokenAuthentication,))
@permission_classes((permissions.IsAuthenticated,))
class NewSession(APIView):

    def post(self, request, format=None):

        user = request.user

        session = Session.create(user.profile)
        session.save()

        user_token = Token.objects.get_or_create(user=user)

        return Response({'sessionToken': session.token, 'authorizationToken': user_token[0].key}, status=status.HTTP_201_CREATED)




class SessionOptionsSerializer(serializers.ModelSerializer):

    class Meta:
        model = Session
        fields = ('column_number', 'has_tsm', 'learner', 'classifier')


@authentication_classes((TokenAuthentication,))
@permission_classes((permissions.IsAuthenticated,))
class SessionOptions(APIView):

    def get_object(self, token):
        try:
            return Session.objects.get(token=token)
        except Session.DoesNotExist:
            raise Http404

    def put(self, request, token, format=None):


        session = self.get_object(token)
        serializer = SessionOptionsSerializer(session, data=request.data)

        if (serializer.is_valid()):
            serializer.save()
            return Response(serializer.data)

            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)



class NewLabelSerializer(serializers.ModelSerializer):

    class Meta:
        model = Session
        fields = ('number_of_labeled_points',)



@authentication_classes((TokenAuthentication,))
@permission_classes((permissions.IsAuthenticated,))
class NewLabel(APIView):

    def get_object(self, token):
        try:
            return Session.objects.get(token=token)
        except Session.DoesNotExist:
            raise Http404

    def put(self, request, token, format=None):


        session = self.get_object(token)
        serializer = NewLabelSerializer(session, data=request.data)

        if (serializer.is_valid()):
            serializer.save()
            return Response(serializer.data)

            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)