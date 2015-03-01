<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import="emot.utils.EmotUtils"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>How do you feel about this link?</title>
<style>

body {
height: 100%;
}

.formHolder {
height:20%; 
width:100%
}

.footer {
padding: 10px;
}

.iFrameHolder {
margin-top: 20px;
}

iframe {
margin-top: 20px;
border: 1; 
width:100%; 
height:80%;
}

label {
margin: 0 0 10px 0;
padding: 0;
display: block; 
font-size: x-small;
text-align: center;
}

.sm {
float: left;
width: 40px;
margin:0px 5px 0px 10px;

}

  button {
	outline: 0;
	border: 0;
	border-width:0;
	background-color: #fff;
	color: #fff;
    background-repeat: no-repeat;
    background-position: 50% 50%;
    background-size:40px 40px;
	width:40px;
	height:40px;
}

 button.happy {
    background-image: url('http://www.emot.com/img/happy.gif');
}
button.happy:hover,
button.happy:active,
button.happy:focus {
    background-image: url('http://www.emot.com/img/happy_h.gif');
}

 button.happy {
    background-image: url('http://www.emot.com/img/happy.gif');
}
button.happy:hover,
button.happy:active,
button.happy:focus {
    background-image: url('http://www.emot.com/img/happy_h.gif');
}


 button.happy {
    background-image: url('http://www.emot.com/img/happy.gif');
}
button.happy:hover,
button.happy:active,
button.happy:focus {
    background-image: url('http://www.emot.com/img/happy_h.gif');
}


button.sad {
    background-image: url('http://www.emot.com/img/sad.gif');
}
button.sad:hover,
button.sad:active,
button.sad:focus {
    background-image: url('http://www.emot.com/img/sad_h.gif');
}


button.surprised {
    background-image: url('http://www.emot.com/img/supprised.gif');
}
button.surprised:hover,
button.surprised:active,
button.surprised:focus {
    background-image: url('http://www.emot.com/img/supprised_h.gif');
}


button.worried {
    background-image: url('http://www.emot.com/img/worried.gif');
}
button.worried:hover,
button.worried:active,
button.worried:focus {
    background-image: url('http://www.emot.com/img/worried_h.gif');
}

button.disgusted {
    background-image: url('http://www.emot.com/img/disgusted.gif');
}
button.disgusted:hover,
button.disgusted:active,
button.disgusted:focus {
    background-image: url('http://www.emot.com/img/disgusted_h.gif');
}

button.angry {
    background-image: url('http://www.emot.com/img/angry.gif');
}
button.angry:hover,
button.angry:active,
button.angry:focus {
    background-image: url('http://www.emot.com/img/angry_h.gif');
}

</style>
</head>
<body>
<%
String url =  request.getRequestURI();
url = url.substring(url.lastIndexOf("/")+1,url.length());
String[] infoArr = EmotUtils.getInfo(url);
%>
<div class="formHolder">
<form action="../Poll" method="get">
<input type="hidden" name="url" value="<%=url%>">
<input type="hidden" name="re" value="<%=infoArr[2]%>">
<div class="text">
This link makes me:
</div>
 <div class="sm">
 <label for="Happy">Happy</label>
 <button class="emoticon happy" value="Happy" name="emot" type="submit"></button>
 </div>
 
 <div class="sm">
  <label for="Sad">Sad</label>
 <button class="emoticon sad" name="emot" type="submit" value="Sad" id="Sad"></button>
 </div>
 
 <div class="sm">
  <label for="Surprised">Surprised</label>
 <button class="emoticon surprised" name="emot" type="submit" value="Surprised" id="Surprised"></button>

 </div>
 
 <div class="sm">
  <label for="Worried">Worried</label>
  <button class="emoticon worried" name="emot" type="submit" value="Worried" id="Worried"></button>
 </div>
 
 <div class="sm">
  <label for="Disgusted">Disgusted</label>
 <button class="emoticon disgusted" name="emot" type="submit" value="Disgusted" id="Disgusted"></button>

 </div>
 
 <div class="sm">
 <label for="Angry">Angry</label>
 <button class="emoticon angry" name="emot" type="submit" value="Angry" id="Angry"></button>
 </div>
</form>
</div>

<div class="iFrameHolder">
<iframe src='<%=infoArr[2]%>'></iframe>
</div>
<div class="footer">
footer
</div>
</body>
</html>
