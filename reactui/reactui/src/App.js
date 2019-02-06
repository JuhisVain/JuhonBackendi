import React, { Component } from 'react';
import './App.css';

class App extends Component {

	constructor () {
		super()
		this.queryCity = ""
		this.launchQuery = this.launchQuery.bind(this)
	}
	
	launchQuery () {
		var xmlhr = new XMLHttpRequest();
		xmlhr.onreadystatechange = function() {
			if (this.readyState === 4 && this.status === 200) {
				var parsedJson = JSON.parse(this.responseText)
				document.getElementById("weatherp").innerHTML =
				"The weather in " + parsedJson.id +
				" is " +parsedJson.temperature+ "C " +
				parsedJson.weather + "."
			}
		}
		
		xmlhr.open("GET", "http://localhost/weather?q="+this.queryCity, true)
		xmlhr.send()
	}

	render () {
		return (<div className='maindiv'>
	<input type="text"
			onInput={e => this.queryCity = e.target.value}
			id="cityField"/>
	<button className='button'
		onClick={this.launchQuery}>
		Execute query
	</button>
	<p id="weatherp">
	</p>
</div>
		)
	}
}

export default App;
