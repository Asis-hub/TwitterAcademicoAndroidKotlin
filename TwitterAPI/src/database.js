const mysql = require('mysql')

const mysqlConnection = mysql.createConnection({
  host: 'reww4n.xyz',
  user: 'luisapi',
  password: 'Carvajal31',
  database: 'luis'
})

mysqlConnection.connect(function (err) {
  if (err) {
    console.log(err)
  } else {
    console.log('BD Conectada')
  }
})

module.exports = mysqlConnection
