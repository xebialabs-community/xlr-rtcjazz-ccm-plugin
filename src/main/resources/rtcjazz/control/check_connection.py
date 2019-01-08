# Copyright (c) 2019 XebiaLabs
# 
# This software is released under the MIT License.
# https://opensource.org/licenses/MIT

from com.xebialabs.xlrelease.plugin.rtcjazz.ccm import RtcRepository
from com.xebialabs.xlrelease.plugin.rtcjazz.ccm import IWorkItemConstants

def process(task_vars):
    container = task_vars['server']
    client = RtcRepository.new_instance(container["url"], container["username"], container["password"])
    
    result = client.checkConnection()
    print(result)

    return result

if __name__ == '__main__' or __name__ == '__builtin__':
    process(locals())
