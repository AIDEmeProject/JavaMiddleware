from django import forms
from django_registration.forms import RegistrationForm

from user.models import User


class Email(forms.EmailField):
    def clean(self, value):
        super(Email, self).clean(value)
        try:
            User.objects.get(email=value)
            raise forms.ValidationError("This email is already registered. Use the 'forgot password' link on the login page")
        except User.DoesNotExist:
            return value




class MyCustomUserForm(RegistrationForm):
    password1 = forms.CharField(widget=forms.PasswordInput(), label="Password")
    password2 = forms.CharField(widget=forms.PasswordInput(), label="Repeat your password")
    # email will be become username
    email = Email()


    class Meta(RegistrationForm.Meta):
        model = User

    def clean_password(self):
        if self.data['password1'] != self.data['password2']:
            raise forms.ValidationError('Passwords are not the same')
        return self.data['password1']