# Copyright (c) 2019 XebiaLabs
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

# Reference: https://docs.oasis-open.org/oslc-domains/cm/v3.0/cs02/part1-change-mgt/cm-v3.0-cs02-part1-change-mgt.html

# rtcjazz/core/RtcClient.py

import sets
import sys
import unicodedata
import logging
import com.xhaus.jyson.JysonCodec as json
import xml.etree.ElementTree as ET

from rtcjazz.core.HttpRequestPlus import HttpRequestPlus

HTTP_SUCCESS = sets.Set([200, 201, 204])

NAMESPACES = {
    'oslc_disc': 'http://open-services.net/xmlns/discovery/1.0/',
    'dcterms': 'http://purl.org/dc/terms/',
    'rdf': 'http://www.w3.org/1999/02/22-rdf-syntax-ns#'
}

WORKITEM_BASE_URI = '/ccm/oslc/contexts/${context}/workitems/'
# Note: keys must match workitem_type property in synthetic.xml
WORKITEM_FACTORY_URI = { 
    'Defect': WORKITEM_BASE_URI+'com.ibm.team.workitem.workItemType.defect',
    'Issue': WORKITEM_BASE_URI+'com.ibm.team.workitem.workItemType.issue',
    'Risk': WORKITEM_BASE_URI+'com.ibm.team.workitem.workItemType.risk',
    'Task': WORKITEM_BASE_URI+'com.ibm.team.workitem.workItemType.task'
}
WORKITEM_UPDATE_URI = '/ccm/resource/itemName/com.ibm.team.workitem.WorkItem/${workitem_id}'
WORKITEM_COMMENT_URI = '/ccm/resource/itemName/com.ibm.team.workitem.WorkItem/${workitem_id}/rtc_cm:comments'

logging.basicConfig(filename='log/plugin.log',
                            filemode='a',
                            format='%(asctime)s,%(msecs)d %(name)s %(levelname)s %(message)s',
                            datefmt='%H:%M:%S',
                            level=logging.DEBUG)

class RtcClient(object):
    httpRequest = None
    logger = logging.getLogger('RtcClient')

    def __init__(self, httpConnection, username=None, password=None):
        self.logger.info('__init__ : %s' % repr(httpConnection))
        self.httpRequest = HttpRequestPlus(httpConnection, username, password)

    @staticmethod
    def createClient(httpConnection, username=None, password=None):
        return RtcClient(httpConnection, username, password)

    def check_connection(self):
        contentType = "application/json"
        headers = {'Accept' : 'application/json', 'Content-Type' : 'application/json'}
        url = '/ccm/oslc-scm/catalog'

        response = self.httpRequest.get(url, contentType=contentType, headers=headers)
        if response.getStatus() not in HTTP_SUCCESS:
            self._error('Unable to check connection', response)

    # Status: gives 403, forbidden. Syntax may not be correct, see https://jazz.net/forum/questions/180335/adding-comments-using-oslc
    def add_comment(self, project_area, workitem_id, comment):
        contentType = "application/json"
        headers = {'Accept' : 'application/json', 'Content-Type' : 'application/json'}
        body = {"dc:description": comment}
        url = WORKITEM_COMMENT_URI.replace('${workitem_id}', workitem_id)

        response = self.httpRequest.get(url, contentType=contentType, headers=headers)
        if response.getStatus() not in HTTP_SUCCESS:
            self._error('Unable to add comment', response)

    # Status: likely works
    def update_workitem(self, workitem_id, properties):
        contentType = "application/json"
        headers = {'Accept' : 'application/json', 'Content-Type' : 'application/json'}
        body = { }
        body.update(properties)
        url = WORKITEM_UPDATE_URI.replace('${workitem_id}', workitem_id)

        self.logger.debug('update_workitem:  url: %s' % url)

        response = self.httpRequest.put(url, body, contentType=contentType, headers=headers)
        self.logger.debug('update_workitem: response: %s' % repr(response))
        if response.getStatus() not in HTTP_SUCCESS:
            self.logger.error('update_workitem: status: %s' % response.getStatus())
            self._error('Unable to update workitem', response)

    # Status: gives 403, forbidden.  post on draft workitems also gives 403
    def create_workitem(self, project_area, workitem_type, title, description, properties):
        context_id = self._get_context_id(project_area)
        if context_id is None:
            self._error('Project area "%s" not found.' % project_area)

        self.logger.debug('create_workitem: context id: "%s"' % context_id)
        print 'Creating work item in project "%s"' % context_id

        contentType = "application/json"
        headers = {'Accept' : 'application/json', 'Content-Type' : 'application/json'}
        body = { 'dc:title': title, 'dc:description': description }
        body.update(properties)
        url = WORKITEM_FACTORY_URI[workitem_type].replace('${context}', context_id)

        self.logger.debug('create_workitem: body: %s' % body)
        self.logger.debug('create_workitem:  url: %s' % url)

        response = self.httpRequest.post(url, body, contentType=contentType, headers=headers)
        self.logger.debug('create_workitem: response: %s' % repr(response))
        if response.getStatus() not in HTTP_SUCCESS:
            self.logger.error('create_workitem: status: %s' % response.getStatus())
            self._error('Unable to create workitem', response)

        # extract identifier
        self.logger.debug('create_workitem: parse response as json')
        data = json.loads(response.response)
        self.logger.info('create_workitem:  data: %s' % data)

        return data['dc:identifier']

    # private methods ---------------------------------

    # get the project context id give the project area
    def _get_context_id(self, project_area):
        contentType = "application/json"
        headers = {'Accept' : 'application/json', 'Content-Type' : 'application/json'}
        url = '/ccm/oslc-scm/catalog'

        response = self.httpRequest.get(url, contentType=contentType, headers=headers)
        if response.getStatus() not in HTTP_SUCCESS:
            self._error('Unable to create work item, catalog request failed.', response)

        ascii_xml = self._strip_unicode(response.response)
        root = ET.fromstring(ascii_xml)

        # find project area
        for srvprov in root.iterfind('.//oslc_disc:ServiceProvider', NAMESPACES):
            for child in srvprov:
                if child.tag == '{http://purl.org/dc/terms/}title' and child.text == project_area:
                    url = srvprov.find('./oslc_disc:details', NAMESPACES).attrib.values()[0]
                    return url[url.rfind('/')+1:]
    
        return None


    def _strip_unicode(self, input_str):
        nfkd_form = unicodedata.normalize('NFKD', input_str)
        only_ascii = nfkd_form.encode('ASCII', 'ignore')
        return only_ascii


    def _error(self, text, response=None):
        if response is not None:
            self.logger.error('Http Request Error : %s : %s' % (text, repr(response)))
            response.errorDump()
        else:
            self.logger.error('Error : %s : ' % text)

        raise Exception(text)
