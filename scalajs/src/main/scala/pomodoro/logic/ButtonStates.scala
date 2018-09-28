package pomodoro.logic

import pomodoro.model.wsmessage.{EndBreak, EndPomodoro, StartBreak, StartPomodoro}

private[logic] class ButtonStates(wsClient: WebSocketClient) {

  private[logic] val idle = Controls(
    ButtonProps("",
      "Start Pomodoro",
      "btn-outline-success",
      _ => wsClient.sendMessage(StartPomodoro)),
    ButtonProps("d-none", "Stop", "btn-outline-danger", _ => ())
  )

  private[logic] val running = Controls(
    ButtonProps("",
      "Start Break",
      "btn-outline-warning",
      _ => wsClient.sendMessage(StartBreak("break"))),
    ButtonProps("",
      "Stop Pomodoro",
      "btn-outline-danger",
      _ => wsClient.sendMessage(EndPomodoro))
  )

  private[logic] val runningOvertime = Controls(
    ButtonProps("",
      "Start Break",
      "btn-outline-success",
      _ => wsClient.sendMessage(StartBreak("break"))),
    ButtonProps("",
      "Stop Pomodoro",
      "btn-outline-danger",
      _ => wsClient.sendMessage(EndPomodoro))
  )

  private[logic] val break = Controls(
    ButtonProps("",
      "Stop Break",
      "btn-outline-warning",
      _ => wsClient.sendMessage(EndBreak)),
    ButtonProps("d-none", "Stop Pomodoro", "btn-outline-danger", _ => ())
  )

}
