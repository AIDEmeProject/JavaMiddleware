# Generated by Django 2.1.4 on 2019-01-09 10:24

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('restapi', '0001_initial'),
        ('user', '0001_initial'),
    ]

    operations = [
        migrations.AddField(
            model_name='session',
            name='user',
            field=models.ForeignKey(on_delete='cascade', to='user.Profile'),
        ),
    ]