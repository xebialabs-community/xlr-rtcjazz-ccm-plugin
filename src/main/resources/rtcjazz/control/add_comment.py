# Copyright (c) 2019 XebiaLabs
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

# rtcjazz/control/add_comment.py

from rtcjazz.core.RtcClient import RtcClient

def process(task_vars):
    server = task_vars['server']
    client = RtcClient.createClient(server, server["username"], server["password"])
    
    result = client.create_comment(task_vars['project_area'], task_vars['workitem_id'], task_vars['comment'])

    print(result)
    return result

if __name__ == '__main__' or __name__ == '__builtin__':
    process(locals())
