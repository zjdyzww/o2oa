/** ***** BEGIN LICENSE BLOCK *****
 * |------------------------------------------------------------------------------|
 * | O2OA 活力办公 创意无限    o2m.api.js                                            |
 * |------------------------------------------------------------------------------|
 * | Distributed under the AGPL license:                                          |
 * |------------------------------------------------------------------------------|
 * | Copyright © 2018, o2oa.net, o2server.io O2 Team                              |
 * | All rights reserved.                                                         |
 * |------------------------------------------------------------------------------|
 *
 *  This file is part of O2OA.
 *
 *  O2OA is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  O2OA is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ******/

(function () {
  this.o2m = {
    version: {
      v: "1.0.0",
      build: "2019.04.20",
      info: "O2OA 活力办公 创意无限. Copyright © 2019, o2oa.net O2 Team All rights reserved."
    },
    log: function(message) {
      window.o2android&&window.o2android.o2mLog ? window.o2android.o2mLog(message) : window.webkit.messageHandlers.o2mLog.postMessage(message);
    }
  }

/** ***** BEGIN NOTIFICATION BLOCK *****
  notification 模块   
    alert
    confirm
    prompt
    vibrate
    toast
    actionSheet
    showLoading
    hideLoading
* ***** END NOTIFICATION BLOCK ******/

  this.o2m.notification = {};

  var _notification_post = function(body, onFail) {
    if (body == null) {
      if (onFail && typeof onFail === "function") {
        onFail("参数异常！")
        return
      }
    }
    var message = JSON.stringify(body);
    if ((window.o2mNotification&&window.o2mNotification.postMessage) || (window.webkit.messageHandlers.o2mNotification)) {
      window.o2mNotification&&window.o2mNotification.postMessage ? window.o2mNotification.postMessage(message) : window.webkit.messageHandlers.o2mNotification.postMessage(message);
    }else {
      if (onFail && typeof onFail === "function") {
        onFail("请在O2OA移动端使用！")
        return
      }
    }
   
  }

  // notification.alert
  this.o2m.notification.alertSuccess = function(){
    console.log("notification alert back");
  };
  var _o2m_n_alert = function(alert) {
    var message = alert&&alert.message ? alert.message : "";
    var title = alert&&alert.title ? alert.title : "";
    var buttonName = alert&&alert.buttonName ? alert.buttonName : "";
    var onSuccess  = alert&&alert.onSuccess ? alert.onSuccess : null;
    var onFail  = alert&&alert.onFail ? alert.onFail : null;
    if (message === "") {
      if (typeof onFail === "function") { onFail("消息内容不能为空！"); }
      return ;
    }
    if(onSuccess && typeof onSuccess === "function"){
      o2m.notification.alertSuccess = onSuccess;
    }
    var body = {
      type: "alert",
      callback: "o2m.notification.alertSuccess",
      data: {
        message: message,
        title: title,
        buttonName: buttonName,
      }
    };
    _notification_post(body, onFail);
  };
  this.o2m.notification.alert = _o2m_n_alert;

  //notification.confirm
  this.o2m.notification.confirmSuccess = function(index) {
    console.log("notification confirm back, click button index: "+index);
  };
  var _o2m_n_confirm = function(c) {
    var buttonLabels = c&&c.buttonLabels ? c.buttonLabels : ["确定" ,"取消"];
    var message = c&&c.message ? c.message : "";
    var title = c&&c.title ? c.title : "";
    var onSuccess  = c&&c.onSuccess ? c.onSuccess : null;
    var onFail  = c&&c.onFail ? c.onFail : null;
    if (message === "") {
      if (typeof onFail === "function") { onFail("消息内容message不能为空！"); }
      return ;
    }
    if (buttonLabels.length != 2) {
      if (typeof onFail === "function") { onFail("按钮名称数组长度只能是2！"); }
      return ;
    }
    if(onSuccess && typeof onSuccess === "function"){
      o2m.notification.confirmSuccess = onSuccess;
    }
    var body = {
      type: "confirm",
      callback: "o2m.notification.confirmSuccess",
      data: {
        message: message,
        title: title,
        buttonLabels: buttonLabels,
      }
    };
    _notification_post(body, onFail);
  }
  this.o2m.notification.confirm = _o2m_n_confirm;

  //notification.prompt
  this.o2m.notification.promptSuccess = function(result) {
    console.log("notification prompt back, click button result: "+result);
  };
  var _o2m_n_prompt = function(c) {
    var buttonLabels = c&&c.buttonLabels ? c.buttonLabels : ["确定" ,"取消"];
    var message = c&&c.message ? c.message : "";
    var title = c&&c.title ? c.title : "";
    var onSuccess  = c&&c.onSuccess ? c.onSuccess : null;
    var onFail  = c&&c.onFail ? c.onFail : null;
    if (message === "") {
      if (typeof onFail === "function") { onFail("消息内容message不能为空！"); }
      return ;
    }
    if (buttonLabels.length != 2) {
      if (typeof onFail === "function") { onFail("按钮名称数组长度只能是2！"); }
      return ;
    }
    if(onSuccess && typeof onSuccess === "function"){
      o2m.notification.promptSuccess = onSuccess;
    }
    var body = {
      type: "prompt",
      callback: "o2m.notification.promptSuccess",
      data: {
        message: message,
        title: title,
        buttonLabels: buttonLabels,
      }
    };
    _notification_post(body, onFail);
  }
  this.o2m.notification.prompt = _o2m_n_prompt;


  //notification.vibrate
  this.o2m.notification.vibrateSuccess = function() {
    console.log("notification vibrate back, click button");
  };
  var _o2m_n_vibrate = function(c) {
    var duration = c&&c.duration ? c.duration : 300;
    var onSuccess  = c&&c.onSuccess ? c.onSuccess : null;
    var onFail  = c&&c.onFail ? c.onFail : null;
    
    if(onSuccess && typeof onSuccess === "function"){
      o2m.notification.vibrateSuccess = onSuccess;
    }
    var body = {
      type: "vibrate",
      callback: "o2m.notification.vibrateSuccess",
      data: {
        duration: duration
      }
    };
    _notification_post(body, onFail);
  }
  this.o2m.notification.vibrate = _o2m_n_vibrate;

  //notification.toast
  this.o2m.notification.toastSuccess = function() {
    console.log("notification toast back, click button");
  };
  var _o2m_n_toast = function(c) {
    var duration = c&&c.duration ? c.duration : 300;
    var message = c&&c.message ? c.message : "";
    var onSuccess  = c&&c.onSuccess ? c.onSuccess : null;
    var onFail  = c&&c.onFail ? c.onFail : null;
    if (message === "") {
      if (typeof onFail === "function") { onFail("消息内容message不能为空！"); }
      return ;
    }
    if(onSuccess && typeof onSuccess === "function"){
      o2m.notification.toastSuccess = onSuccess;
    }
    var body = {
      type: "toast",
      callback: "o2m.notification.toastSuccess",
      data: {
        duration: duration,
        message: message
      }
    };
    _notification_post(body, onFail);
  }
  this.o2m.notification.toast = _o2m_n_toast;

  //notification.actionSheet
  this.o2m.notification.actionSheetSuccess = function(buttonIndex) {
    console.log("notification actionSheet back, click button:"+buttonIndex);
  };
  var _o2m_n_actionSheet = function(c) {
    var title = c&&c.title ? c.title : "";
    var cancelButton = c&&c.cancelButton ? c.cancelButton : "取消";
    var otherButtons = c&&c.otherButtons ? c.otherButtons : [];
    var onSuccess  = c&&c.onSuccess ? c.onSuccess : null;
    var onFail  = c&&c.onFail ? c.onFail : null;
    if (title === "") {
      if (typeof onFail === "function") { onFail("title标题不能为空！"); }
      return ;
    }
    if (otherButtons.length < 1) {
      if (typeof onFail === "function") { onFail("其他按钮列表不能为空！"); }
      return ;
    }
    if(onSuccess && typeof onSuccess === "function"){
      o2m.notification.actionSheetSuccess = onSuccess;
    }
    var body = {
      type: "actionSheet",
      callback: "o2m.notification.actionSheetSuccess",
      data: {
        title: title,
        cancelButton: cancelButton,
        otherButtons: otherButtons
      }
    };
    _notification_post(body, onFail);
  }
  this.o2m.notification.actionSheet = _o2m_n_actionSheet;

  //notification.showLoading
  this.o2m.notification.showLoadingSuccess = function() {
    console.log("notification showLoading back");
  };
  var _o2m_n_showLoading = function(c) {
    var text = c&&c.text ? c.text : "";
    var onSuccess  = c&&c.onSuccess ? c.onSuccess : null;
    var onFail  = c&&c.onFail ? c.onFail : null;
    if(onSuccess && typeof onSuccess === "function"){
      o2m.notification.showLoadingSuccess = onSuccess;
    }
    var body = {
      type: "showLoading",
      callback: "o2m.notification.showLoadingSuccess",
      data: {
        text: text
      }
    };
    _notification_post(body, onFail);
  }
  this.o2m.notification.showLoading = _o2m_n_showLoading;

  //notification.hideLoading
  this.o2m.notification.hideLoadingSuccess = function() {
    console.log("notification hideLoading back");
  };
  var _o2m_n_hideLoading = function(c) {
    var onSuccess  = c&&c.onSuccess ? c.onSuccess : null;
    var onFail  = c&&c.onFail ? c.onFail : null;
    if(onSuccess && typeof onSuccess === "function"){
      o2m.notification.hideLoadingSuccess = onSuccess;
    }
    var body = {
      type: "hideLoading",
      callback: "o2m.notification.hideLoadingSuccess",
      data: {
      }
    };
    _notification_post(body, onFail);
  }
  this.o2m.notification.hideLoading = _o2m_n_hideLoading;

  



/** ***** BEGIN UTIL BLOCK *****
  util 模块   
    date
      o2m.util.date.datePicker
      o2m.util.date.timePicker
      o2m.util.date.dateTimePicker
    calendar
      o2m.util.calendar.chooseOneDay
      o2m.util.calendar.chooseDateTime
      o2m.util.calendar.chooseInterval



* ***** END UTIL BLOCK ******/

this.o2m.util = {
  date: {},
  calendar: {}
};

var _util_post = function(body, onFail) {
  if (body == null) {
    if (onFail && typeof onFail === "function") {
      onFail("参数异常！")
      return
    }
  }
  var message = JSON.stringify(body);
  if ((window.o2mUtil.postMessage&&window.o2mUtil.postMessage) || (window.webkit.messageHandlers.o2mUtil)) {
    window.o2mUtil&&window.o2mUtil.postMessage ? window.o2mUtil.postMessage(message) : window.webkit.messageHandlers.o2mUtil.postMessage(message);
  }else {
    if (onFail && typeof onFail === "function") {
      onFail("请在O2OA移动端使用！")
      return
    }
  }
}

//o2m.util.date.datePicker
this.o2m.util.date.datePickerSuccess = function(result) {
  console.log("util date datePicker back, result:"+result);
};
var _o2m_u_date_datePicker = function(c) {
  var onSuccess  = c&&c.onSuccess ? c.onSuccess : null;
  var onFail  = c&&c.onFail ? c.onFail : null;
  var value = c&&c.value ? c.value : "";
  if(onSuccess && typeof onSuccess === "function"){
    o2m.util.date.datePickerSuccess = onSuccess;
  }
  var body = {
    type: "date.datePicker",
    callback: "o2m.util.date.datePickerSuccess",
    data: {
      value: value
    }
  };
  _util_post(body, onFail);
}
this.o2m.util.date.datePicker = _o2m_u_date_datePicker;

//o2m.util.date.timePicker
this.o2m.util.date.timePickerSuccess = function(result) {
  console.log("util date timePicker back, result:"+result);
};
var _o2m_u_date_timePicker = function(c) {
  var onSuccess  = c&&c.onSuccess ? c.onSuccess : null;
  var onFail  = c&&c.onFail ? c.onFail : null;
  var value = c&&c.value ? c.value : "";
  if(onSuccess && typeof onSuccess === "function"){
    o2m.util.date.timePickerSuccess = onSuccess;
  }
  var body = {
    type: "date.timePicker",
    callback: "o2m.util.date.timePickerSuccess",
    data: {
      value: value
    }
  };
  _util_post(body, onFail);
}
this.o2m.util.date.timePicker = _o2m_u_date_timePicker;



//o2m.util.date.dateTimePicker
this.o2m.util.date.dateTimePickerSuccess = function(result) {
  console.log("util date dateTimePicker back, result:"+result);
};
var _o2m_u_date_dateTimePicker = function(c) {
  var onSuccess  = c&&c.onSuccess ? c.onSuccess : null;
  var onFail  = c&&c.onFail ? c.onFail : null;
  var value = c&&c.value ? c.value : "";
  if(onSuccess && typeof onSuccess === "function"){
    o2m.util.date.dateTimePickerSuccess = onSuccess;
  }
  var body = {
    type: "date.dateTimePicker",
    callback: "o2m.util.date.dateTimePickerSuccess",
    data: {
      value: value
    }
  };
  _util_post(body, onFail);
}
this.o2m.util.date.dateTimePicker = _o2m_u_date_dateTimePicker;



//o2m.util.calendar.chooseOneDay
this.o2m.util.calendar.chooseOneDaySuccess = function(result) {
  console.log("util calendar chooseOneDay back, result:"+result);
};
var _o2m_u_calendar_chooseOneDay = function(c) {
  var onSuccess  = c&&c.onSuccess ? c.onSuccess : null;
  var onFail  = c&&c.onFail ? c.onFail : null;
  var value = c&&c.value ? c.value : "";
  if(onSuccess && typeof onSuccess === "function"){
    o2m.util.calendar.chooseOneDaySuccess = onSuccess;
  }
  var body = {
    type: "calendar.chooseOneDay",
    callback: "o2m.util.calendar.chooseOneDaySuccess",
    data: {
      value: value
    }
  };
  _util_post(body, onFail);
}
this.o2m.util.calendar.chooseOneDay = _o2m_u_calendar_chooseOneDay;


//o2m.util.calendar.chooseDateTime
this.o2m.util.calendar.chooseDateTimeSuccess = function(result) {
  console.log("util calendar chooseDateTime back, result:"+result);
};
var _o2m_u_calendar_chooseDateTime = function(c) {
  var onSuccess  = c&&c.onSuccess ? c.onSuccess : null;
  var onFail  = c&&c.onFail ? c.onFail : null;
  var value = c&&c.value ? c.value : "";
  if(onSuccess && typeof onSuccess === "function"){
    o2m.util.calendar.chooseDateTimeSuccess = onSuccess;
  }
  var body = {
    type: "calendar.chooseDateTime",
    callback: "o2m.util.calendar.chooseDateTimeSuccess",
    data: {
      value: value
    }
  };
  _util_post(body, onFail);
}
this.o2m.util.calendar.chooseDateTime = _o2m_u_calendar_chooseDateTime;


//o2m.util.calendar.chooseInterval
this.o2m.util.calendar.chooseIntervalSuccess = function(result) {
  console.log("util calendar chooseInterval back, result:"+result);
};
var _o2m_u_calendar_chooseInterval = function(c) {
  var onSuccess  = c&&c.onSuccess ? c.onSuccess : null;
  var onFail  = c&&c.onFail ? c.onFail : null;
  var startDate = c&&c.startDate ? c.startDate : "";
  var endDate = c&&c.endDate ? c.endDate : "";
  if(onSuccess && typeof onSuccess === "function"){
    o2m.util.calendar.chooseIntervalSuccess = onSuccess;
  }
  var body = {
    type: "calendar.chooseInterval",
    callback: "o2m.util.calendar.chooseIntervalSuccess",
    data: {
      startDate: startDate,
      endDate: endDate
    }
  };
  _util_post(body, onFail);
}
this.o2m.util.calendar.chooseInterval = _o2m_u_calendar_chooseInterval;


})();
