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
	
	
exports.reOrdering = functions.database
	.ref('/QuestList/{user_no}/{wildcard}')
	.onDelete(event => {		
		return promise = event.data.ref.parent.once('value')
			.then(snap => {
				var count = 1
				snap.forEach(function(childSnapshot){
					var keys = childSnapshot.key
					if (keys != count && keys != 'Count' && keys != 'ID') {
						event.data.ref.parent.child(count).set(childSnapshot.val())	
					}
					count++
				})
				event.data.ref.parent.child(count-2).remove()
				event.data.ref.parent.child('Count').set(count-3)
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
  
  
  
  
  
  
  