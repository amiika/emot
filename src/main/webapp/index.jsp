<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>emot.com - URL enhancer</title>
<style>

input[type="url"]{
 	border: 1px solid #ccc;
	-moz-border-radius: 10px;
    -webkit-border-radius: 10px;
	border-radius: 10px;
	padding: 8px;
	padding-right: 45px;
}

input:required{
	background: url("img/mandatory.png") no-repeat 96% center white;
	outline: 2px solid transparent;
}
input:valid{
	background: url("img/valid.png") no-repeat 96% center white;

}
input:invalid:focus{
	background: url("img/invalid.png") no-repeat 96% center white;
	outline: 1px solid red;
}
input[type="submit"]:valid{
	outline-color: transparent;
	box-shadow: 1px 1px 2px rgba(0,0,0,0.3);
	border-radius: 2px;
	background-image: none;
	padding-right: 8px;
}
input[type="submit"]:valid:hover,
input[type="submit"]:valid:focus{
	cursor: pointer;
	box-shadow: none;
}

.formHolder {
 margin-top: 100px;
  width: 400px;
  margin-left: auto ;
  margin-right: auto;
}

label {
margin: 0;
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

.text {
float:left;
margin:10px 5px 0px 10px;
font-size: small;
vertical-align:middle;
}

.emoticon {
margin: 0;
width:40px;
height:40px;
vertical-align:middle;
}

fieldset { 
border:1px solid black;
  -moz-border-radius:10px;  
  border-radius: 10px;  
  -webkit-border-radius: 10px; 
  display: inline-block;
}

.url {
margin: 10px;
}

legend {
  padding: 0.2em 0.5em;
  border:1px solid black;
  color: black;
  font-size:90%;
  font-family: comic-sans;
  text-align:left; 
  -moz-border-radius:10px;  
  border-radius: 10px;  
  -webkit-border-radius: 10px; 
  }
</style>
</head>
<body>

<div class="formHolder">
 
 <!--  Validation is crappy, change to jQuery form validation() ? -->
<form action="Shorten" method="get">
<fieldset>
<legend>Emot url</legend>
<div class="url">
<input value="http://" type="url" name="url" style="width:450px;" required></input>
</div>

<div class="text">
This link makes me:
</div>
 <div class="sm">
 <input class="emoticon" name="emot" value="Happy" type="image" src="img/Happy.svg"  id="Happy" alt="Happy" />
 <label for="Happy">Happy</label>
 </div>
 
 <div class="sm">
 <input class="emoticon" name="emot" type="image"  value="Sad" id="Sad" src="img/Sad.svg"  alt="Sad" />
 <label for="Sad">Sad</label>
 </div>
 
 <div class="sm">
 <input class="emoticon" name="emot" type="image" value="Surprised" id="Surprised" src="img/Surprised.svg"  alt="Suprised" />
 <label for="Surprised">Surprised</label>
 </div>
 
 <div class="sm">
 <input class="emoticon" name="emot"type="image" value="Worried" id="Worried" src="img/Worried.svg"  alt="Worried" />
 <label for="Worried">Worried</label>
 </div>
 
 <div class="sm">
 <input class="emoticon" name="emot" type="image" value="Disgusted" id="Disgusted" src="img/Disgusted.svg"  alt="Disgusted" />
 <label for="Disgusted">Disgusted</label>
 </div>
 
 <div class="sm">
 <input class="emoticon" name="emot" type="image" value="Angry" id="Angry" src="img/Angry.svg"  alt="Angry" />
 <label for="Angry">Angry</label>
 </div>

</fieldset>
</form>
<div>
<img src="img/emotcom.png"/>
</div>
</div>
</body>
</html>