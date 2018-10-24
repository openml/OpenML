$(function() {
	var canvas = $("#c");
	var canvasHeight;
	var canvasWidth;
	var ctx;
	
	var pointCollection;
	
	function init() {
		updateCanvasDimensions();
		
		var g = [new Point(35, 75, 0.0, 5, "#ed9d33"), new Point(27, 68, 0.0, 4.5, "#ed9d33"), new Point(22, 59, 0.0, 4, "#ed9d33"), new Point(22, 47, 0.0, 3.5, "#ed9d33"), new Point(24, 35, 0.0, 3, "#ed9d33"), new Point(29, 25, 0.0, 2.5, "#ed9d33"), new Point(36, 15, 0.0, 2, "#ed9d33"), new Point(37, 63, 0.0, 5, "#d44d61"), new Point(33, 52, 0.0, 4.5, "#d44d61"), new Point(33, 41, 0.0, 4, "#d44d61"), new Point(37, 30, 0.0, 3.5, "#d44d61"), new Point(43, 20, 0.0, 3, "#d44d61"), new Point(49, 12, 0.0, 2.5, "#d44d61"), new Point(58, 4, 0.0, 2, "#d44d61"), new Point(43, 83, 0.0, 5, "#4f7af2"), new Point(30, 84, 0.0, 4.5, "#4f7af2"), new Point(20, 77, 0.0, 4, "#4f7af2"), new Point(14, 68, 0.0, 3.5, "#4f7af2"), new Point(11, 58, 0.0, 3, "#4f7af2"), new Point(13, 47, 0.0, 2.5, "#4f7af2"), new Point(16, 36, 0.0, 2, "#4f7af2"), new Point(56, 85, 0.0, 5, "#269230"), new Point(47, 92, 0.0, 4.5, "#269230"), new Point(36, 93, 0.0, 4, "#269230"), new Point(25, 92, 0.0, 3.5, "#269230"), new Point(15, 87, 0.0, 3, "#269230"), new Point(8, 78, 0.0, 2.5, "#269230"), new Point(5, 69, 0.0, 2, "#269230")];
		
		gLength = g.length;
		for (var i = 0; i < gLength; i++) {
			g[i].curPos.x = (canvasWidth/2 - 140) + g[i].curPos.x;
			g[i].curPos.y = (140 - 50) + g[i].curPos.y;
			i
			g[i].originalPos.x = (canvasWidth/2 - 140) + g[i].originalPos.x;
			g[i].originalPos.y = (140 - 50) + g[i].originalPos.y;
		};
		
		pointCollection = new PointCollection();
		pointCollection.points = g;
	};
	
	function initEventListeners() {
		$(window).bind('resize', updateCanvasDimensions).bind('mousemove', onMove);

		canvas.get(0).ontouchmove = function(e) {
			e.preventDefault();
			onTouchMove(e);
		};
		
		canvas.get(0).ontouchstart = function(e) {
			e.preventDefault();
		};
	};
	
	function updateCanvasDimensions() {
		canvas.attr({height: $(window).height(), width: ($(window).width()-10)});
		canvasWidth = window.innerWidth;
		canvasHeight = canvas.height();

		draw();
	};
	
	function onMove(e) {
		if (pointCollection)
			pointCollection.mousePos.set(e.pageX, e.pageY);
	};
	
	function onTouchMove(e) {
		if (pointCollection)
			pointCollection.mousePos.set(e.targetTouches[0].pageX, e.targetTouches[0].pageY);
	};
	
	function timeout() {
		draw();
		update();
		
		setTimeout(function() { timeout() }, 30);
	};
	
	function draw() {
		var tmpCanvas = canvas.get(0);

		if (tmpCanvas.getContext == null) {
			return; 
		};
		
		ctx = tmpCanvas.getContext('2d');
		ctx.clearRect(0, 0, canvasWidth, canvasHeight);
		
		if (pointCollection)
			pointCollection.draw();
	};
	
	function update() {		
		if (pointCollection)
			pointCollection.update();
	};
	
	function Vector(x, y, z) {
		this.x = x;
		this.y = y;
		this.z = z;
 
		this.addX = function(x) {
			this.x += x;
		};
		
		this.addY = function(y) {
			this.y += y;
		};
		
		this.addZ = function(z) {
			this.z += z;
		};
 
		this.set = function(x, y, z) {
			this.x = x; 
			this.y = y;
			this.z = z;
		};
	};
	
	function PointCollection() {
		this.mousePos = new Vector(0, 0);
		this.points = new Array();
		
		this.newPoint = function(x, y, z) {
			var point = new Point(x, y, z);
			this.points.push(point);
			return point;
		};
		
		this.update = function() {		
			var pointsLength = this.points.length;
			
			for (var i = 0; i < pointsLength; i++) {
				var point = this.points[i];
				
				if (point == null)
					continue;
				
				var dx = this.mousePos.x - point.curPos.x;
				var dy = this.mousePos.y - point.curPos.y;
				var dd = (dx * dx) + (dy * dy);
				var d = Math.sqrt(dd);
				
				if (d < 50) {
					point.targetPos.x = (this.mousePos.x < point.curPos.x) ? point.curPos.x - dx*5 : point.curPos.x - dx*5;
					point.targetPos.y = (this.mousePos.y < point.curPos.y) ? point.curPos.y - dy*5 : point.curPos.y - dy*5;
				} else {
					point.targetPos.x = point.originalPos.x;
					point.targetPos.y = point.originalPos.y;
				};
				
				point.update();
			};
		};
		
		this.draw = function() {
			var pointsLength = this.points.length;
			for (var i = 0; i < pointsLength; i++) {
				var point = this.points[i];
				
				if (point == null)
					continue;

				point.draw();
			};
		};
	};
	
	function Point(x, y, z, size, colour) {
		this.colour = colour;
		this.curPos = new Vector(x, y, z);
		this.friction = 0.8;
		this.originalPos = new Vector(x, y, z);
		this.radius = size;
		this.size = size;
		this.springStrength = 0.1;
		this.targetPos = new Vector(x, y, z);
		this.velocity = new Vector(0.0, 0.0, 0.0);
		
		this.update = function() {
			var dx = this.targetPos.x - this.curPos.x;
			var ax = dx * this.springStrength;
			this.velocity.x += ax;
			this.velocity.x *= this.friction;
			this.curPos.x += this.velocity.x;
			
			var dy = this.targetPos.y - this.curPos.y;
			var ay = dy * this.springStrength;
			this.velocity.y += ay;
			this.velocity.y *= this.friction;
			this.curPos.y += this.velocity.y;
			
			var dox = this.originalPos.x - this.curPos.x;
			var doy = this.originalPos.y - this.curPos.y;
			var dd = (dox * dox) + (doy * doy);
			var d = Math.sqrt(dd);
			
			this.targetPos.z = d/100 + 1;
			var dz = this.targetPos.z - this.curPos.z;
			var az = dz * this.springStrength;
			this.velocity.z += az;
			this.velocity.z *= this.friction;
			this.curPos.z += this.velocity.z;
			
			this.radius = this.size*this.curPos.z;
			if (this.radius < 1) this.radius = 1;
		};
		
		this.draw = function() {
			ctx.fillStyle = this.colour;
			ctx.beginPath();
			ctx.arc(this.curPos.x, this.curPos.y, this.radius, 0, Math.PI*2, true);
			ctx.fill();
		};
	};
	
	init();
	initEventListeners();
	timeout();


$(window).resize(function(e) {
        init();
});
});

