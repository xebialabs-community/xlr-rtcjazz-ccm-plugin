#!flask/bin/python

# Copyright (c) 2019 XebiaLabs
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT


from flask import Flask
from flask import request, Response
from flask import make_response
from functools import wraps
import os, io, json

app = Flask(__name__)

def getFile( fileName, status="200" ):
    filePath = "/mockserver/responses/%s" % fileName
    if not os.path.isfile(filePath):
        raise AuthError({"code": "response_file_not_found", "description": "Unable to load response file"}, 500)

    f = io.open(filePath, "r", encoding="utf-8")

    resp = make_response( (f.read(), status) )

    if fileName.endswith('.json'):
        resp.headers['Content-Type'] = 'application/json; charset=utf-8'
    elif fileName.endswith('.xml'):
        resp.headers['Content-Type'] = 'application/xml; charset=utf-8'

    return resp

def check_auth(username, password):
    """This function is called to check if a username /
    password combination is valid.
    """
    return username == 'xlr_test' and password == 'admin'

def authenticate():
    """Sends a 401 response that enables basic auth"""
    return Response(
    'Could not verify your access level for that URL.\n'
    'You have to login with proper credentials', 401,
    {'WWW-Authenticate': 'Basic realm="Login Required"'})

def requires_auth(f):
    """
    Determines if the basic auth is valid
    """
    @wraps(f)
    def decorated(*args, **kwargs):
        auth = request.authorization
        if not auth or not check_auth(auth.username, auth.password):
            return authenticate()
        return f(*args, **kwargs)
    return decorated


@app.route('/')
def index():
    return "Hello, from Mock Server!"


@app.route('/ccm/rootservices', methods=['GET'])
@requires_auth
def getCcmRootservices():
    return getFile("ccm_rootservices.json")


@app.route('/ccm/oslc-scm/catalog', methods=['GET'])
def getCcmCatalog():
    return getFile("ccm_catalog.xml")


@app.route('/ccm/oslc/workitems/_IGnd8P2_EeiFnpGGc4js6g', methods=['GET'])
def getCcmWorkitem():
    return getFile("ccm_workitem.json")

https://dev2developer.aetna.com/ccm/oslc/workitems/_IGnd8P2_EeiFnpGGc4js6g/rtc_cm:comments

@app.route('/ccm/oslc/workitems/_IGnd8P2_EeiFnpGGc4js6g/rtc_cm:comments', methods=['GET'])
def getCcmWorkitemComments():
    return getFile("ccm_workitem_comments.json")


if __name__ == '__main__':
    app.run(debug=True)
