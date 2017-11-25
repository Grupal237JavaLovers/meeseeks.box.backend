# Meeseeks Box

## Contributing

#### Guide

The Back-End Checklist repository consists of two branches:

1. master
2. develop

Always use *develop* in order to push your code.

#### Get Started

1.  Start a MySql Server with the name "meeseeks" on port 3306.
2.  Create a database named "meeseeks".
3.  Replace the default password in the application.properties.example and change the extension to ".properties"
4.  Run *MeeseeksBox.class*
5.  Go to http://localhost:8080/ to test your REST contribution.

## Testing using Swagger

You can access the Swagger documentation at the following URL: http://localhost:8080/swagger-ui.html
Here you will find all the controllers and all the actions available, as well as what parameters an actions needs and what responses it returns.

You can also test any action from the browser.Keep in mind that the documentation is not perfect, for example for actions which have a model as a parameter and also return a model, the Swagger will show both the read and write properties in the same model, even though you should not pass some properties when creating an object. (ex: /consumer/register does not require *created*, *id*, *role* as parameters)

#### Using JWT

To use JWT authentication when testing using the Swagger documentation, you need to first login using a REST client and then get the token (after the Bearer) and put it in your **application.properties** file under the **app.security.token** key.After you restart the app, every request to the backend will be made using the token.
