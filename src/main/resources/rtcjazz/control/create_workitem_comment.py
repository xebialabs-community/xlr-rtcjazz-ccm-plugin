# Copyright (c) 2019 XebiaLabs
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

from com.xebialabs.xlrelease.plugin.rtcjazz.ccm import RtcRepository
from com.xebialabs.xlrelease.plugin.rtcjazz.ccm import IWorkItemConstants

def process(task_vars):
    properties = {}
    properties.update(task_vars['workitem_properties'])

    properties[IWorkItemConstants.SWITCH_IGNOREERRORS] = task_vars['workitem_ignore_errors']
    properties[IWorkItemConstants.SWITCH_SUPPRESS_MAIL_NOTIFICATION] = task_vars['workitem_suppress_email_notifications']
    properties[IWorkItemConstants.SWITCH_ENABLE_DELETE_ATTACHMENTS] = task_vars['workitem_enable_delete_attachments']
    properties[IWorkItemConstants.SWITCH_ENABLE_DELETE_APPROVALS] = task_vars['workitem_enable_delete_approvals']
    properties[IWorkItemConstants.SWITCH_ENFORCE_SIZE_LIMITS] = task_vars['workitem_enforce_size_limits']

    container = task_vars['server']
    client = RtcRepository.new_instance(container["url"], container["username"], container["password"])
    
    result = client.update_workitem(task_vars['project_area'], task_vars['workitem_id'], properties)
    print(result)


if __name__ == '__main__' or __name__ == '__builtin__':
    process(locals())
