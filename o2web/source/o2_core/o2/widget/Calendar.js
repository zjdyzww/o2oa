o2.widget = o2.widget || {};
o2.widget.Calendar = o2.Calendar = new Class({
	Implements: [Options, Events],
    Extends: o2.widget.Common,
	options: {
		"style": "default",
		"path": o2.session.path+"/widget/$Calendar/" ,

		"defaultView": "day", //day, month, year
		"baseDate": new Date(),
		"isTime": false,
		"isMulti": false,
		"before": null,
		"after": null,
		"timeOnly": false,
		"defaultDate": new Date(),
		
		"beforeCurrent": true,

		"range": false,
		"rangeNodes": [],
		"rangeRule": "asc",  //asc + ,  des -
        "target": null
	},
	initialize: function(node, options){
		Locale.use("zh-CHS");
		this.options.defaultTime = ""+this.options.baseDate.getHours()+":"+this.options.baseDate.getMinutes()+":"+this.options.baseDate.getSeconds();
		this.setOptions(options);

		this.path = o2.session.path+"/widget/$Calendar/";
		this.cssPath = o2.session.path+"/widget/$Calendar/"+this.options.style+"/css.wcss";
		
		this._loadCss();
	//	this.options.containerPath = this.path+this.style+"/container.html";
	//	this.options.dayPath = this.path+this.style+"/day.html";
	//	this.options.monthPath = this.path+this.style+"/month.html";
	//	this.options.yearPath = this.path+this.style+"/year.html";
	//	this.options.timePath = this.path+this.style+"/time.html";

        if (!this.options.format){
            if (this.options.isTime){
                //this.options.format = Locale.get("Date").shortDate + " " + Locale.get("Date").shortTime;
                if(this.options.timeOnly){
                    this.options.format="%H:%M";
                }
                else{
                    this.options.format = Locale.get("Date").shortDate + " " + "%H:%M";
                }
            }else{
                this.options.format = Locale.get("Date").shortDate;
            }
        }
		
		this.options.containerPath = this.options.path+this.options.style+"/container.html";
		this.options.dayPath = this.options.path+this.options.style+"/day.html";
		this.options.monthPath = this.options.path+this.options.style+"/month.html";
		this.options.yearPath = this.options.path+this.options.style+"/year.html";
		this.options.timePath = this.options.path+this.options.style+"/time.html";
	
		this.today = new Date();
		
		this.currentView = this.options.defaultView;
		
		this.node = $(node);

		this.visible = false;



		this.container = this.createContainer();


		this.container.inject((this.options.target) || $(document.body));

		this.contentTable = this.createContentTable();
		this.contentTable.inject(this.contentDateNode);

		this.addEvents();
		this.container.set({
			styles: {
				"display": "none",
				"opacity": 1
			}
		});
		this.fireEvent("init");

		//this.move = true;
		//this.containerDrag = new Drag.Move(this.container);
	},
	addEvents: function(){
		this.node.addEvent("focus", function(){
			this.show();
		}.bind(this));
		this.node.addEvent("click", function(){
			this.show();
		}.bind(this));

		this.prevNode.addEvent("click", function(){
			this.getPrev();
		}.bind(this));

		this.nextNode.addEvent("click", function(){
			this.getNext();
		}.bind(this));

		this.currentTextNode.addEvent("click", function(){
			this.changeView();
		}.bind(this));

		this.titleNode.addEvent("mousedown", function(){
			this.move();
		}.bind(this));
		this.titleNode.addEvent("mouseup", function(){
			this.unmove();
		}.bind(this));

		document.addEvent('mousedown', this.outsideClick.bind(this));
	},

	move: function(){
		this.containerDrag = new Drag.Move(this.container, {
            "onDrag": function(e){
                if (this.iframe){
                    var p = this.container.getPosition();
                    this.iframe.setStyles({
						"top": ""+p.y+"px",
                        "left": ""+p.x+"px"
					});
				}
            }.bind(this)
		});
	},
	unmove: function(){
		this.container.removeEvents("mousedown");
		this.titleNode.addEvent("mousedown", function(){
			this.move();
		}.bind(this));
	},

	changeView: function(){
		var view = "day";
		switch (this.currentView) {
			case "day" :
				this.changeViewToMonth();
				break;
			case "month" :
				this.changeViewToYear();
				break;
			case "year" :
				this.changeViewToDay();
				break;
			case "time" :
				this.changeViewToDay();
				//this.changeViewToDay();
				break;
			default :
				//nothing;
		}
	},
	changeViewToMonth: function(year){
		this.currentView = "month";
		
		if (!this.contentMonthTable){
			this.contentMonthTable = this.createContentTable();
			this.contentMonthTable.inject(this.contentDateNode);
		}
		if (this.contentTable) this.contentTable.setStyle("display", "none");
		if (this.contentYearTable) this.contentYearTable.setStyle("display", "none");
		if (this.contentTimeTable) this.contentTimeTable.setStyle("display", "none");
	//	if (this.contentMonthTable) this.contentMonthTable.setStyle("display", "block");
		if (this.contentMonthTable) this.contentMonthTable.setStyle("display", "table");

		var year = (year!=undefined) ? year : this.currentTextNode.retrieve("year");
		var month = this.currentTextNode.retrieve("month");

		this.showMonth(year, month);
	},
	changeViewToYear: function(year){
		this.currentView = "year";
		
		if (!this.contentYearTable){
			this.contentYearTable = this.createContentTable();
			this.contentYearTable.inject(this.contentDateNode);
		}
		if (this.contentTable) this.contentTable.setStyle("display", "none");
		if (this.contentMonthTable) this.contentMonthTable.setStyle("display", "none");
		if (this.contentTimeTable) this.contentTimeTable.setStyle("display", "none");
	//	if (this.contentYearTable) this.contentYearTable.setStyle("display", "block");
		if (this.contentYearTable) this.contentYearTable.setStyle("display", "table");

		this.showYear(year);
	},
	changeViewToDay: function(year, month){
		this.currentView = "day";
		
		if (!this.contentTable){
			this.contentTable = this.createContentTable();
			this.contentTable.inject(this.contentDateNode);
		}

		if (this.contentMonthTable) this.contentMonthTable.setStyle("display", "none");
		if (this.contentYearTable) this.contentYearTable.setStyle("display", "none");
		if (this.contentTimeTable) this.contentTimeTable.setStyle("display", "none");
	//	if (this.contentTable) this.contentTable.setStyle("display", "block");
		if (this.contentTable) this.contentTable.setStyle("display", "table");

		this.showDay(year, month);
	},
	getNext: function(){
		switch (this.currentView) {
            case "time" :
                this.getNextDate();
                break;
			case "day" :
				this.getNextDay();
				break;
			case "month" :
				this.getNextMonth();
				break;
			case "year" :
				this.getNextYear();
				break;
			default :
				//nothing
		}
	},

	getPrev: function(){
		switch (this.currentView) {
            case "time" :
                this.getPrevDate();
                break;
			case "day" :
				this.getPrevDay();
				break;
			case "month" :
				this.getPrevMonth();
				break;
			case "year" :
				this.getPrevYear();
				break;
			default :
				//nothing
		}
	},
    getNextDate: function(){
        var date = this.currentTextNode.retrieve("date");
        // var year = this.currentTextNode.retrieve("year");
        // var month = this.currentTextNode.retrieve("month");
        // month--;
        // var day = this.currentTextNode.retrieve("day");
        // var date = new Date(year, month, day);
        date.increment("day", 1);
        this._setTimeTitle(null, date);
    },
    getPrevDate: function(){
        var date = this.currentTextNode.retrieve("date");
        date.increment("day", -1);
        this._setTimeTitle(null, date);
    },
	getNextDay: function(){
		var year = this.currentTextNode.retrieve("year");
		var month = this.currentTextNode.retrieve("month");
		month--;
		var date = new Date(year, month, 1);
		date.increment("month", 1);

		var thisYear = date.getFullYear();
		var thisMonth = date.getMonth();
		
		this._setDayTitle(null, thisYear, thisMonth);
		this._setDayDate(null,thisYear, thisMonth);
	},

	getPrevDay: function(){
		var year = this.currentTextNode.retrieve("year");
		var month = this.currentTextNode.retrieve("month");
		month--;
		var date = new Date(year, month, 1);
		date.increment("month", -1)

		var thisYear = date.getFullYear();
		var thisMonth = date.getMonth();

		this._setDayTitle(null, thisYear, thisMonth);
		this._setDayDate(null,thisYear, thisMonth);
	},

	getNextMonth: function(){
		var year = this.currentTextNode.retrieve("year");
		var date = new Date(year, 1, 1);
		date.increment("year", 1)

		var thisYear = date.getFullYear();
		
		this.showMonth(thisYear);
	},
	getPrevMonth: function(){
		var year = this.currentTextNode.retrieve("year");
		var date = new Date(year, 1, 1);
		date.increment("year", -1)

		var thisYear = date.getFullYear();
		
		this.showMonth(thisYear);
	},
	getNextYear: function(){
		var year = this.currentTextNode.retrieve("year");
		var date = new Date(year, 1, 1);
		date.increment("year", this.yearLength)

		var thisYear = date.getFullYear();
		
		this.showYear(thisYear);
	},
	getPrevYear: function(){
		var year = this.currentTextNode.retrieve("year");
		var date = new Date(year, 1, 1);
		date.increment("year", 0-this.yearLength)

		var thisYear = date.getFullYear();
		
		this.showYear(thisYear);
	},

	outsideClick: function(e) {
		if(this.visible) {
			var elementCoords = this.container.getCoordinates();
			var targetCoords  = this.node.getCoordinates();
			if(((e.page.x < elementCoords.left || e.page.x > (elementCoords.left + elementCoords.width)) ||
			    (e.page.y < elementCoords.top || e.page.y > (elementCoords.top + elementCoords.height))) &&
			   ((e.page.x < targetCoords.left || e.page.x > (targetCoords.left + targetCoords.width)) ||
			    (e.page.y < targetCoords.top || e.page.y > (targetCoords.top + targetCoords.height))) ) this.hide();
		}
	},
	
	hide: function(){
		if (this.visible){
//			if (!this.morph){
//				this.morph = new Fx.Morph(this.container, {"duration": 200});
//			}
			this.visible = false;
	//		this.changeViewToDay();
//			this.morph.start({"opacity": 0}).chain(function(){
				this.container.setStyle("display", "none");
				if (this.iframe) this.iframe.destroy();
				debugger;

            if (layout.desktop.offices){
                Object.each(layout.desktop.offices, function(office){
                    office.show();
                });
            }
//			}.bind(this));
			this.fireEvent("hide");
		}
	},
	show: function(){
		if (!this.visible){
			var dStr = this.node.get("value");
			if (dStr && Date.isValid(dStr)){
				this.options.baseDate = Date.parse(dStr.substr(0,10));
			}
			if(this.options.timeOnly){
				this.currentView = "time";
			}
			else{
				this.currentView = this.options.defaultView;
			}
			
			switch (this.currentView) {
				case "day" :
					this.changeViewToDay();
					break;
				case "month" :
					this.showMonth();
					break;
				case "year" :
					this.showYear();
					break;
				case "time" :
					//this.showTime(this.options.baseDate);
					this.changeViewToTime(this.options.defaultDate);
					//this.changeViewToTime(this.options.baseDate);
					break;
				default :
					this.showDay();
			}

//			if (!this.morph){
//				this.morph = new Fx.Morph(this.container, {"duration": 200});
//			}
			this.container.setStyle("display", "block");

            if (this.container.position){
                this.container.position({
                    relativeTo: this.node,
                    position: 'bottomLeft',
                    edge: 'upperLeft'
                });
 //               var offsetPNode = this.node.getOffsetParent();

                var cp = this.container.getPosition(this.options.target || null);
                var cSize = this.container.getSize();
                //var fp = (this.options.target) ? this.options.target.getPosition() : $(document.body).getPosition()
                var fsize = (this.options.target) ? this.options.target.getSize() : $(document.body).getSize();

                //if (cp.y+cSize.y>fsize.y+fp.y){
                if (cp.y+cSize.y>fsize.y){
                    this.container.position({
                        relativeTo: this.node,
                        position: 'upperLeft',
                        edge: 'bottomLeft'
                    });
                }
            }else{
                var p = this.node.getPosition(this.options.target || null);
                var size = this.node.getSize();
                var containerSize = this.container.getSize();
                var bodySize = $(document.body).getSize();

                var left = p.x;
                if ((left + containerSize.x) > bodySize.x){
                	left = bodySize.x - containerSize.x;
                }

                this.container.setStyle("top", p.y+size.y+2);
                this.container.setStyle("left", left);
            }
            // var p = this.container.getPosition();
            // var s = this.container.getSize();
            // var zidx = this.container.getStyle("z-index");
            // this.iframe = new Element("iframe", {"styles":{
            //     "border": "0px",
            //     "margin": "0px",
            //     "padding": "0px",
            //     "opacity": 0,
				// "z-index": (zidx) ? zidx-1 : 0,
				// "top": ""+p.y+"px",
            //     "left": ""+p.x+"px",
            //     "width": ""+s.x+"px",
            //     "height": ""+s.y+"px",
				// "position": "absolute"
            // }}).inject(this.container, "before");

			if (layout.desktop.offices){
                Object.each(layout.desktop.offices, function(office){
                    if (this.container.isOverlap(office.officeNode)){
                        office.hide();
					}
				}.bind(this));
			}

//			this.morph.start({"opacity": 1}).chain(function(){
				this.visible = true;
//			}.bind(this));
			this.fireEvent("show");
		}
	},
	showYear: function(year){
		var thisYear = (year!=undefined) ? year : this.options.baseDate.getFullYear();

		var date = new Date(thisYear, 1, 1);
		date.increment("year", -2);
		var beginYear = date.getFullYear();
		date.increment("year", this.yearLength-1);
		var endYear = date.getFullYear();
		
		this._setYearTitle(null, beginYear, endYear, thisYear);
		this._setYearDate(null, beginYear, endYear, thisYear);

	//	if (!this.move){
	//		this.move = true;
	//		this.containerDrag = new Drag.Move(this.container);
	//	}
	},
	_setYearTitle:function(node, beginYear, endYear, thisYear){
		var thisNode = node || this.currentTextNode;
		thisNode.set("text", beginYear+"-"+endYear);	
		thisNode.store("year", thisYear);
	},
	_setYearDate: function(table, beginYear, endYear, year){
		var yearTable = table || this.contentYearTable;

		var thisYear = (year!=undefined) ? year : this.options.baseDate.getFullYear();

		var tbody = yearTable.getElement("tbody");
		var tds = tbody.getElements("td");

		tds.each(function(item, idx){
			var y = beginYear+idx;
			item.set("text", y);
			item.store("year", y);
			if (y==this.options.baseDate.getFullYear()){
				item.addClass("current_"+this.options.style);
			}else{
				item.removeClass("current_"+this.options.style);
			}
		}.bind(this));
	},
	showMonth: function(year, month){
		var thisYear = (year!=undefined) ? year : this.options.baseDate.getFullYear();
		var thisMonth = (month!=undefined) ? month : this.options.baseDate.getMonth();

		this._setMonthTitle(null, thisYear, thisMonth);
		this._setMonthDate(null, thisYear, thisMonth);

	//	if (!this.move){
	//		this.move = true;
	//		this.containerDrag = new Drag.Move(this.container);
	//	}
	},
	_setMonthTitle:function(node, year){
		var thisYear = (year!=undefined) ? year : this.options.baseDate.getFullYear();
		var thisNode = node || this.currentTextNode;
		thisNode.set("text", thisYear);	
		thisNode.store("year", thisYear);
	},
	_setMonthDate: function(table, year, month){
		//var months = Locale.get("Date").months;
        var months = o2.LP.widget.months;
		var monthTable = table || this.contentMonthTable;

		var thisYear = (year!=undefined) ? year : this.options.baseDate.getFullYear();
		var thisMonth = (month!=undefined) ? month : this.options.baseDate.getMonth();

		var tbody = monthTable.getElement("tbody");
		var tds = tbody.getElements("td");

		tds.each(function(item, idx){
			item.set("text", months[idx].substr(0,2));
			item.store("year", thisYear);
			item.store("month", idx);
			if ((thisYear==this.options.baseDate.getFullYear()) && (idx==this.options.baseDate.getMonth())){
				item.addClass("current_"+this.options.style);
			}else{
				item.removeClass("current_"+this.options.style);
			}
		}.bind(this));
	},

	showDay: function(year, month){
		this._setDayTitle(null, year, month);
		this._setDayWeekTitleTh();
		this._setDayDate(null, year, month);

	//	if (!this.move){
	//		this.move = true;
	//		this.containerDrag = new Drag.Move(this.container);
	//	}
	},
	_setDayTitle: function(node, year, month){
		var thisYear = (year!=undefined) ? year : this.options.baseDate.getFullYear();
		var thisMonth = (month!=undefined) ? month : this.options.baseDate.getMonth();
		thisMonth++;

		var text = thisYear+"年"+thisMonth+"月";
		var thisNode = node || this.currentTextNode;
		thisNode.set("text", text);
		
		thisNode.store("year", thisYear);
		thisNode.store("month", thisMonth);
	},
	_setDayDate: function(table, year, month){
		var dayTable = table || this.contentTable;
		var baseDate = this.options.baseDate;
		if ((year!=undefined) && (month!=undefined)){
			baseDate = new Date();
			baseDate.setDate(1);
			baseDate.setFullYear(year);
			baseDate.setMonth(month);
		}

		var tbody = dayTable.getElement("tbody");
		var tds = tbody.getElements("td");

		var firstDate = baseDate.clone();
		firstDate.setDate(1);
		var day = firstDate.getDay();
		
		var tmpDate = firstDate.clone();
		for (var i=day-1; i>=0; i--){
			tmpDate.increment("day", -1);
			tds[i].set("text", tmpDate.getDate());
			tds[i].addClass("gray_"+this.options.style);
			tds[i].setStyles(this.css["gray_"+this.options.style]);
			tds[i].store("dateValue", tmpDate.toString())
		}
		
		for (var i=day; i<tds.length; i++){
			tds[i].set("text", firstDate.getDate());
			if (firstDate.toString() == this.options.baseDate.toString()){
				tds[i].addClass("current_"+this.options.style);
				tds[i].setStyles(this.css["current_"+this.options.style]);

				tds[i].removeClass("gray_"+this.options.style);
				tds[i].setStyle("border", "1px solid #FFF");
			}else if (firstDate.getMonth()!=baseDate.getMonth()){
				tds[i].addClass("gray_"+this.options.style);
				tds[i].setStyles(this.css["gray_"+this.options.style]);
				tds[i].removeClass("current_"+this.options.style);
				tds[i].setStyle("border", "1px solid #FFF");
			}else{
				tds[i].setStyles(this.css["normal_"+this.options.style]);
				tds[i].removeClass("current_"+this.options.style);
				tds[i].removeClass("gray_"+this.options.style);
				tds[i].setStyle("border", "1px solid #FFF");
			}
			var tmp = firstDate.clone();
			if (tmp.clearTime().toString() == this.today.clearTime().toString()){
				//tds[i].addClass("today_"+this.options.style);
				tds[i].setStyles(this.css["today_"+this.options.style]);
				tds[i].setStyle("border", "0px solid #AAA");
			}
			tds[i].store("dateValue", firstDate.toString())
			firstDate.increment("day", 1);
		}
	},
	
	changeViewToTime: function(date){
		this.currentView = "time";
		
		if (!this.contentTimeTable){
			this.contentTimeTable = this.createContentTable();
			this.contentTimeTable.inject(this.contentDateNode);
		}
		if (this.contentTable) this.contentTable.setStyle("display", "none");
		if (this.contentYearTable) this.contentYearTable.setStyle("display", "none");
		if (this.contentMonthTable) this.contentMonthTable.setStyle("display", "none");
		if (this.contentTimeTable) this.contentTimeTable.setStyle("display", "block");
	//	if (this.contentTimeTable) this.contentTimeTable.setStyle("display", "table");

		var thisDate = date || this.options.baseDate;

		this.showTime(thisDate); 
	},

	showTime: function(date){
	//	var thisHour = this.options.baseDate.getHours();
	//	var thisMinutes = this.options.baseDate.getMinutes();
	//	var thisSeconds = this.options.baseDate.getSeconds();
		var times = this.options.defaultTime.split(":");

		var thisHour = (times[0]) ? times[0] : "0";
		var thisMinutes = (times[1]) ? times[1] : "0";
		var thisSeconds = (times[2]) ? times[2] : "0";
		
		this._setTimeTitle(null, date);
		this._setTimeDate(null, thisHour, thisMinutes, thisSeconds);

	//	if (this.move){
	//		this.move = false;
	//		this.container.removeEvents("mousedown");
	//	}
	},

	_setTimeTitle: function(node, date){
		var thisDate = date || this.options.baseDate;
		var thisNode = node || this.currentTextNode;

		var y = thisDate.getFullYear();
		var m = thisDate.getMonth()+1;
		var d = thisDate.getDate();
		var text = "" + y + "年" + m + "月" + d + "日";

		if (this.options.timeOnly){
            thisNode.hide();
            if (this.prevNode) this.prevNode.hide();
            if (this.nextNode) this.nextNode.hide();
		}
		thisNode.set("text", text);	
		thisNode.store("date", date);
	},
	_setTimeDate: function(node, h, m, s){
		this.itmeHNode = this.contentTimeTable.getElement(".MWF_calendar_time_h_slider");
		this.itmeMNode = this.contentTimeTable.getElement(".MWF_calendar_time_m_slider");
	//	this.itmeSNode = this.contentTimeTable.getElement(".MWF_calendar_time_s_slider");

		this.timeShowNode = this.contentTimeTable.getElement(".MWF_calendar_time_show");

		this.timeShowNode.addEvent("click", function(){
			this._selectTime();
		}.bind(this));
		
		this.showHNode = this.contentTimeTable.getElement(".MWF_calendar_time_show_h");
		this.showMNode = this.contentTimeTable.getElement(".MWF_calendar_time_show_m");
	//	this.showSNode = this.contentTimeTable.getElement(".MWF_calendar_time_show_s");
		
		this.showActionNode = this.contentTimeTable.getElement(".MWF_calendar_action_show");
		
		var calendar = this;

		if (COMMON.Browser.Platform.isMobile){
			this.itmeHNode.empty();
			this.itmeHNode.removeClass("calendarTimeSlider");
			this.itmeHNode.setStyles(this.css.calendarTimeSliderNoStyle);
			var sel = new Element("select").inject(this.itmeHNode);
			for (i=0; i<=23; i++){
				var v = (i<10) ? "0"+i: i; 
				var o = new Element("option", {
					"value": v,
					"text": v
				}).inject(sel);
				if (h==i) o.set("selected", true);
			}
			sel.addEvent("change", function(){
				calendar.showHNode.set("text", this.options[this.selectedIndex].get("value"));
			});
			this.showHNode.set("text", sel.options[sel.selectedIndex].get("value"));
			
			this.itmeMNode.empty();
			this.itmeMNode.removeClass("calendarTimeSlider");
			this.itmeMNode.setStyles(this.css.calendarTimeSliderNoStyle);
			sel = new Element("select").inject(this.itmeMNode);
			for (i=0; i<=59; i++){
				var v = (i<10) ? "0"+i: i; 
				var o = new Element("option", {
					"value": v,
					"text": v
				}).inject(sel);
				if (m==i) o.set("selected", true);
			}
			sel.addEvent("change", function(){
				calendar.showMNode.set("text", this.options[this.selectedIndex].get("value"));
			});
			this.showMNode.set("text", sel.options[sel.selectedIndex].get("value"));
		}else{
			var hSlider = new Slider(this.itmeHNode, this.itmeHNode.getFirst(), {
				range: [0, 23],
                initialStep: h.toInt(),
				onChange: function(value){
					var tmp = (value.toInt().toString());
					if (tmp.length<2){
						tmp = "0"+tmp
					}
					this.showHNode.set("text", tmp);
                    this.itmeHNode.getFirst().set("text", tmp);
				}.bind(this)
			});

			var mSlider = new Slider(this.itmeMNode, this.itmeMNode.getFirst(), {
				range: [0, 59],
				initialStep: m.toInt(),
				onChange: function(value){
					var tmp = (value.toInt().toString());
					if (tmp.length<2){
						tmp = "0"+tmp
					}
					this.showMNode.set("text", tmp);
                    this.itmeMNode.getFirst().set("text", tmp);
				}.bind(this)
			});
		}
        this.showHNode.set("text", h.toInt());
        this.showMNode.set("text", m.toInt());

		if (!this.okButton){
			this.okButton = new Element("button", {"text": "确定"}).inject(this.showActionNode);
			this.okButton.addEvent("click", function(){
				this._selectTime();
				this.hide();
			}.bind(this));
			this.okButton.setStyles(this.css.calendarActionShowButton);
		}
		
		if (!this.clearButton){
			this.clearButton = new Element("button", {"text": "清除"}).inject(this.showActionNode);
			this.clearButton.addEvent("click", function(){
				this.node.set("value", "");
                this.fireEvent("clear");
				this.hide();
			}.bind(this));
			this.clearButton.setStyles(this.css.calendarActionShowButton);
		}

		/*	
		var sSlider = new Slider(this.itmeSNode, this.itmeSNode.getFirst(), {
			range: [0, 59],
			initialStep: s,
			onChange: function(value){
				var tmp = new String(value);
				if (tmp.length<2){
					tmp = "0"+tmp
				}
				this.showSNode.set("text", tmp);
			}.bind(this)
		});
		*/
	},
	_selectTime: function(){
		var date = this.currentTextNode.retrieve("date");

		var h = this.showHNode.get("text");
		var m = this.showMNode.get("text");
	//	var s = this.showSNode.get("text");
		date.setHours(h);
		date.setMinutes(m);
	//	date.setSeconds(s);

		if (!this.options.beforeCurrent){
			var now = new Date();
			if (date.getTime()-now.getTime()<0){
				alert("选择的日期必须大于当前日期!");
				this.node.focus();
				return false;
			}
		}
		
		var dv = date.format(this.options.format);

		if (this.fireEvent("queryComplate", [dv, date])){
            var t = this.node.get("value");
			this.node.set("value", dv);
		//	this.node.focus();
			this.hide();
            if (t!=dv) this.fireEvent("change", [dv, date, t]);
			this.fireEvent("complate", [dv, date]);
		}
	},
	_selectDate: function(dateStr){
		var date = new Date(dateStr);
		var dv = date.format(this.options.format);
		if (this.options.isTime){
			this.changeViewToTime(date);
		}else{
			if (!this.options.beforeCurrent){
				var now = new Date();
				date.setHours(23,59,59);
				if (date.getTime()-now.getTime()<0){
					alert("选择的日期必须大于当前日期!");
					this.node.focus();
					return false;
				}
			}
			if (this.fireEvent("queryComplate", [dv, date])){
				var t = this.node.get("value");
				this.node.set("value", dv);
				this.hide();
				if (t!=dv) this.fireEvent("change", [dv, date, t]);
				this.fireEvent("complate", [dv, date, t]);
			}
		}
	},

	_setDayWeekTitleTh: function(table){
		var dayTable = table || this.contentTable;

		var thead = dayTable.getElement("thead");
		var cells = thead.getElements("th");

		if (this.css.calendarDaysContentTh) cells.setStyles(this.css.calendarDaysContentTh);

		//var days_abbr = Locale.get("Date").days_abbr;
        var days_abbr = o2.LP.widget.days_abbr;
		cells.each(function(item, idx){
			item.set("text", days_abbr[idx]);
		});
		return cells;
	},

	createContainer: function(){
		var div = null;
		var request = new Request.HTML({
			url: this.options.containerPath,
			method: "GET",
			async: false,
			onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
				div = responseTree[0];
			}
		});
		request.send();

		//this.containerNode = div.getElement(".MWF_calendar_container"); 
		this.titleNode = div.getElement(".MWF_calendar_title");
		this.prevNode = div.getElement(".MWF_calendar_prev");
		this.currentNode = div.getElement(".MWF_calendar_current");
		this.currentTextNode = div.getElement(".MWF_calendar_currentText");
		this.nextNode = div.getElement(".MWF_calendar_next");
		this.contentNode = div.getElement(".MWF_calendar_content");
		this.contentDateNode = div.getElement(".MWF_calendar_content_date");
		this.contentTimeNode = div.getElement(".MWF_calendar_content_time");
		this.bottomNode = div.getElement(".MWF_calendar_bottom");

		div.setStyles(this.css.container);
		this.titleNode.setStyles(this.css.dateTitle);
		this.prevNode.setStyles(this.css.datePrev);
		this.currentNode.setStyles(this.css.dateCurrent);
		this.currentTextNode.setStyles(this.css.dateCurrentText);
		this.nextNode.setStyles(this.css.dateNext);
		this.contentNode.setStyles(this.css.calendarContent);
		this.bottomNode.setStyles(this.css.dateBottom);

		return div;
	},

	createContentTable: function(){
		var table = null;
		var request = new Request.HTML({
			url: this.options[this.currentView+"Path"],
			method: "GET",
			async: false,
			onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
				table = responseTree[0];
			}
		});
		request.send();

		var tbody = table.getElement("tbody");
		if (tbody){
			var tds = tbody.getElements("td");
		
			var calendar = this;
			tds.addEvent("click", function(){
				switch (calendar.currentView) {
					case "day" :
						calendar._selectDate(this.retrieve("dateValue"), this);
						break;
					case "month" :
						calendar.changeViewToDay(this.retrieve("year"), this.retrieve("month"));
						break;
					case "year" :
						calendar.changeViewToMonth(this.retrieve("year"))
						break;
					case "time" :
						//nothing
						break;
					default :
						//nothing;
				}
				
			});
			
			
			switch (this.currentView) {
				case "day" :
					if (!table.display) table.display="";
					if (!table.style.display) table.style.display="";
					
					table.setStyles(this.css.calendarDaysContent);
					tds.setStyles(this.css.calendarDaysContentTd);
					break;

				case "month" :
					table.setStyles(this.css.calendarMonthsContent);
					tds.setStyles(this.css.calendarMonthsContentTd);
					break;
				case "year" :
					this.yearLength = tds.length;
					table.setStyles(this.css.calendarYearsContent);
					tds.setStyles(this.css.calendarYearsContentTd);
					break;
				case "time" :

					var nodes = table.getElements(".calendarTimeArea");
					if (nodes.length) nodes.setStyles(this.css.calendarTimeArea);

					nodes = table.getElements(".calendarTimeSlider");
					if (nodes.length) nodes.setStyles(this.css.calendarTimeSlider);

					nodes = table.getElements(".calendarTimeSliderKnob");
					if (nodes.length) nodes.setStyles(this.css.calendarTimeSliderKnob);

					nodes = table.getElements(".calendarTimeShow");
					if (nodes.length) nodes.setStyles(this.css.calendarTimeShow);
					
					nodes = table.getElements(".calendarTimeShowItem");
					if (nodes.length) nodes.setStyles(this.css.calendarTimeShowItem);
					
					var node = table.getElement(".MWF_calendar_action_show");
					if (node){
						node.setStyles(this.css.calendarActionShow);
						var buttons = node.getElements("button");
						buttons.setStyles(this.css.calendarActionShowButton);
					} 

					break;
				default :
					//nothing;
			}
			

			tds.addEvent("mouseover", function(){
				this.setStyle("border", "1px solid #999999");
			});
			tds.addEvent("mouseout", function(){
				this.setStyle("border", "1px solid #FFF");
			});
		}else{
			switch (this.currentView) {
				case "day" :
					table.setStyles(this.css.calendarDaysContent);
					tds.setStyles(this.css.calendarDaysContentTd);
					break;
				case "month" :
					table.setStyles(this.css.calendarMonthsContent);
					tds.setStyles(this.css.calendarMonthsContentTd);
					break;
				case "year" :
					this.yearLength = tds.length;
					table.setStyles(this.css.calendarYearsContent);
					tds.setStyles(this.css.calendarYearsContentTd);
					break;
				case "time" :

					var nodes = table.getElements(".calendarTimeArea");
					if (nodes.length) nodes.setStyles(this.css.calendarTimeArea);

					nodes = table.getElements(".calendarTimeSlider");
					if (nodes.length) nodes.setStyles(this.css.calendarTimeSlider);

					nodes = table.getElements(".calendarTimeSliderKnob");
					if (nodes.length) nodes.setStyles(this.css.calendarTimeSliderKnob);

					nodes = table.getElements(".calendarTimeShow");
					if (nodes.length) nodes.setStyles(this.css.calendarTimeShow);
					
					nodes = table.getElements(".calendarTimeShowItem");
					if (nodes.length) nodes.setStyles(this.css.calendarTimeShowItem);
					
					var node = table.getElement(".MWF_calendar_action_show");
					if (node){
						node.setStyles(this.css.calendarActionShow);
						var buttons = node.getElements("button");
						buttons.setStyles(this.css.calendarActionShowButton);
					} 

					break;
				default :
					//nothing;
			}

		}

		return table;
	}
	
});