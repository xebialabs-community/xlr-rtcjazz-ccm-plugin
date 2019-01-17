# Copyright (c) 2019 XebiaLabs
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

# rtcjazz/control/update_workitem.py

from rtcjazz.core.RtcClient import RtcClient

def process(task_vars):
    server = task_vars['server']
    client = RtcClient.createClient(server, server["username"], server["password"])

    properties = {}
    properties.update(task_vars['workitem_properties'])

    result = client.update_workitem(task_vars['project_area'], task_vars['workitem_id'], properties)
    print result
    return result

if __name__ == '__main__' or __name__ == '__builtin__':
    process(locals())
