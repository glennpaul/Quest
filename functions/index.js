const functions = require('firebase-functions');
// The Firebase Admin SDK to access the Firebase Realtime Database. 
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


//example function
//exports.example = functions.database
//	.ref('/QuestList/1/{wildcard}')
//    .onCreate(event => {
//		const count_promise_value = event.data.ref.parent.child("Count").once('value')
//		const value = count_promise_value.then(snap => {
//			return count_value = snap.key		
//		})		
//		const finals = value.then(snap => {
//			event.data.ref.parent.child('home').set(count_value)
//		})
//		return finals
//   })
	
	
exports.order = functions.database
	.ref('/QuestList/{user_no}/{wildcard}')
	.onDelete(event => {		
		return promise = event.data.ref.parent.once('value')
			.then(snap => {
				var data = {}
				var prop_name = ''
				var counts = 1
				snap.forEach(function(childSnapshot){
					var keys = childSnapshot.key
					if (keys == counts || keys == counts + 1) {
						data[counts] = childSnapshot.val()
					} else if (keys == 'Count') {
						data['Count'] = counts-1
					} else {
						data['ID'] = childSnapshot.val()
					}
					counts++
				})
				event.data.ref.parent.set(data)
			})
	})
	
	
exports.createQuestList = functions.auth.user()
	.onCreate(event => {	
		const newUserRef = admin.database().ref().child('/user_count')	
		return promise = newUserRef.once('value')
			.then(snap => {			
				var newv = 1 + snap.val()
				newUserRef.parent.child('QuestList').child(newv).child('ID').set(event.data.uid)
				newUserRef.parent.child('QuestList').child(newv).child('Count').set(0)
				newUserRef.set(newv)
			})
	});
  
  
  
  
  
  
  