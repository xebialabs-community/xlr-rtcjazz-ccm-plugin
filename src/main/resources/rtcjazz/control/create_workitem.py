# Copyright (c) 2019 XebiaLabs
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

# rtcjazz/control/create_workitem.py

from rtcjazz.core.RtcClient import RtcClient

def process(task_vars):
    server = task_vars['server']
    client = RtcClient.createClient(server, server["username"], server["password"])

    return client.create_workitem(task_vars['project_area'], task_vars['workitem_type'], task_vars['title'], task_vars['description'], task_vars['workitem_properties'])


if __name__ == '__main__' or __name__ == '__builtin__':
    workitem_id = process(locals())
