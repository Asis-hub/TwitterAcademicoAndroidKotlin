const express = require('express')
const ruta = express.Router()
const mysqlConnection = require('../database')
const verificarToken = require('../auth')

/**
 * @swagger
 * components:
 *  schemas:
 *    Usuario:
 *      type: object
 *      properties:
 *        idUsuario:
 *          type: integer
 *          description: id autogenerado de Usuario
 *        Nombre:
 *          type: string
 *          description: Nombre del usuario
 *        ApellidoPaterno:
 *          type: string
 *          description: Apellido paterno del usuario
 *        ApellidoMaterno:
 *          type: string
 *          description: Apellido materno del usuario
 *        FechaNacimiento:
 *          type: string
 *          description: fecha de nacimiento del usuario
 *        Email:
 *          type: string
 *          description: Correo electronico del usuario
 *        NombreUsuario:
 *          type: string
 *          description: nombre de usuario para el sistema (Twitter academico)
 *        Contraseña:
 *          type: string
 *          description: contraseña de acceso a la cuenta del usuario
 *        idTipoUsuario:
 *          type: integer
 *          description: id(llave foranea) que representa el roll de este usuario
 *      required:
 *        - Nombre
 *        - ApellidoPaterno
 *        - ApellidoMaterno
 *        - FechaNacimiento
 *        - Email
 *        - NombreUsuario
 *        - Contraseña
 *        - idTipoUsuario
 *  parameters:
 *    idUsuario:
 *      in: path
 *      name: idUsuario
 *      required: true
 *      schema:
 *        type: integer
 *      description: id del Usuario
 */

/**
 * @swagger
 * tags:
 *  name: Usuario
 *  description: endpoints para Usuario
 */

/**
 * @swagger
 * /Usuario:
 *  get:
 *    summary: Permite obtener la lista de usuarios
 *    tags: [Usuario]
 *    responses:
 *      200:
 *        description: Lista de Usuario
 *        content:
 *          application/json:
 *            schema:
 *              type: array
 *              items:
 *                $ref: '#/components/schemas/Usuario'
 */
ruta.get('/Usuario', verificarToken, (req, res) => {
  mysqlConnection.query('CALL R_Usuario()', (err, rows, fields) => {
    if (!err) {
      res.json(rows[0])
    } else {
      res.status(500)
    }
  })
})

/**
 * @swagger
 * /Usuario/{idUsuario}:
 *  get:
 *    summary: Permite obtener un usuario especifico
 *    tags: [Usuario]
 *    parameters:
 *      - $ref: '#/components/parameters/idUsuario'
 *    responses:
 *      200:
 *        description: Usuario eliminado
 *        content:
 *          application/json:
 *            schema:
 *              type: object
 *              items:
 *                $ref: '#/components/schemas/Usuario'
 *      404:
 *        description: Usuario no encontrado
 */
ruta.get('/Usuario/:idUsuario', verificarToken, (req, res) => {
  const { idUsuario } = req.params
  mysqlConnection.query('CALL R_UsuarioByID(?)', [idUsuario], (err, rows, fields) => {
    if (!err) {
      res.status(200).json(rows[0][0])
    } else {
      res.status(500).json(err)
    }
  })
})

ruta.get('/Usuario/Content/:Keyword', verificarToken, (req, res) => {
  const { Keyword } = req.params
  mysqlConnection.query('CALL S_Usuario(?)', [Keyword], (err, rows, fields) => {
    if (!err) {
      res.status(200).json(rows[0])
    } else {
      res.status(500)
    }
  })
})

/**
 * @swagger
 * /Usuario:
 *  post:
 *    summary: Permite registrar un nuevo Usuario
 *    tags: [Usuario]
 *    requestBody:
 *      required: true
 *      content:
 *        application/json:
 *          schema:
 *            $ref: '#/components/schemas/Usuario'
 *    responses:
 *      200:
 *        description: Usuario guardado
 *      500:
 *        description: Error con el servidor
 *
 */
ruta.post('/Usuario', (req, res) => {
  const { idUsuario, FotoPerfil, Nombre, ApellidoPaterno, ApellidoMaterno, FechaNacimiento, Email, NombreUsuario, Password, idTipoUsuario } = req.body
  mysqlConnection.query('CALL CU_Usuario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)', [idUsuario, FotoPerfil, Nombre, ApellidoPaterno, ApellidoMaterno, FechaNacimiento, Email, NombreUsuario, Password, idTipoUsuario], (err, rows, fields) => {
    if (!err) {
      res.status(201).json(rows[0][0])
    } else {
      console.log(err)
    }
  })
})

/**
 * @swagger
 * /Usuario/{idUsuario}:
 *  put:
 *    summary: Permite actualizar un Usuario determinado
 *    tags: [Usuario]
 *    parameters:
 *      - $ref: '#/components/parameters/idUsuario'
 *    requestBody:
 *      required: true
 *      content:
 *        application/json:
 *          schema:
 *            $ref: '#/components/schemas/Usuario'
 *    responses:
 *      200:
 *        description: Usuario actualizado
 *      404:
 *        description: Usuario no encontrado
 */
ruta.put('/Usuario/:idUsuario', verificarToken, (req, res) => {
  const { idUsuario } = req.params
  const { Nombre, FotoPerfil, ApellidoPaterno, ApellidoMaterno, FechaNacimiento, Email, NombreUsuario } = req.body
  mysqlConnection.query('CALL CU_Usuario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)', [idUsuario, FotoPerfil, Nombre, ApellidoPaterno, ApellidoMaterno, FechaNacimiento, Email, NombreUsuario, null, null], (err, rows, fields) => {
    if (!err) {
      res.status(200).json(rows[0][0])
    } else {
      res.status(500)
    }
  })
})

/**
 * @swagger
 * /Usuario/{idUsuario}:
 *  delete:
 *    summary: Permite eliminar un Usuario y todo el contenido relacionado a este
 *    tags: [Usuario]
 *    parameters:
 *      - $ref: '#/components/parameters/idUsuario'
 *    responses:
 *      200:
 *        description: Usuario eliminado
 *      404:
 *        description: Usuario no encontrado
 */
ruta.delete('/Usuario/:idUsuario', verificarToken, (req, res) => {
  const { idUsuario } = req.params
  mysqlConnection.query('CALL D_Usuario(?)', [idUsuario], (err, rows, fields) => {
    if (!err) {
      res.status(200).json(rows[0][0])
    } else {
      res.status(500)
    }
  })
})
module.exports = ruta
