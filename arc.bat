@rem SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
@rem
@rem SPDX-License-Identifier: Apache-2.0
@echo off
title arc
set gradle=gradlew.bat -q --console=plain
if [%1] == [] goto :usage
if %1 == start goto :server_start
if %1 == chat goto :chat_start
:usage
echo Usage of %0:
echo.
echo arc start
echo    Start backend application
echo arc chat [agent]
echo    CLI Chat tool
echo.
exit 1

:server_start
echo "Starting Arc Demo Application..."
title arc server
%gradle% bootRun
exit 0


:chat_start
if [%2] == [] (
    echo Specify an agent
    goto :usage
)
title arc chat %2
echo "Starting conversation with Agent $2..."
%gradle% arc -Pagent="%2"
exit 0