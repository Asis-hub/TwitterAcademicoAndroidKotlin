const config = require('./config')
const jwt = require('jsonwebtoken')
function verificarToken (req, res, next) {
  const token = req.headers['access-token']
  if (!token) {
    return res.status(401).json({ Mensaje: 'Token no proporcionado' })
  } else {
    jwt.verify(token, config.secret, (error) => {
      if (!error) {
        next()
      } else {
        return res.status(403).json({ Mensaje: 'Token invalido' })
      }
    })
  }
}

module.exports = verificarToken
