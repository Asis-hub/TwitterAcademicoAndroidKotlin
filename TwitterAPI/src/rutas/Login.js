const express = require('express')
const ruta = express.Router()
const mysqlConnection = require('../database')
const config = require('../config')
const jwt = require('jsonwebtoken')

/**
 * @swagger
 * components:
 *  schemas:
 *    Login:
 *      type: object
 *      properties:
 *        NombreUsuario:
 *          type: string
 *          description: nombre de usuario para el sistema (Twitter academico)
 *        Contraseña:
 *          type: string
 *          description: contraseña de acceso a la cuenta del usuario
 *      required:
 *        - NombreUsuario
 *        - Contraseña
 */
/**
 * @swagger
 * tags:
 *  name: Login
 *  description: endpoints para Login
 */

/**
 * @swagger
 * /Login:
 *  post:
 *    summary: Permite logearse y obtener un token si las credenciales son validas
 *    tags: [Login]
 *    requestBody:
 *      required: true
 *      content:
 *        application/json:
 *          schema:
 *            $ref: '#/components/schemas/Login'
 *    responses:
 *      200:
 *        description: Logeado
 */
ruta.post('/Login', (req, res) => {
  const { NombreUsuario, Contraseña } = req.body
  mysqlConnection.query('CALL R_Login(?, ?)', [NombreUsuario, Contraseña], (err, rows, fields) => {
    if (!err) {
      if (!rows[0][0].hasOwnProperty('idUsuario')) {
        res.status(200).json(rows[0][0])
      } else {
        rows[0][0].Token = jwt.sign({ User: NombreUsuario, Pss: Contraseña }, config.secret, {
          expiresIn: 60 * 60 * 24
        })
        res.status(201).json(rows[0][0])
      }
    } else {
      res.status(500).json('Error de conexion con el servidor')
    }
  })
})
module.exports = ruta
