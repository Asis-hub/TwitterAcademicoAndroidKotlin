const express = require('express')
const ruta = express.Router()
const { body, validationResult } = require('express-validator')
const mysqlConnection = require('../database')
const verificarToken = require('../auth')

/**
 * @swagger
 * components:
 *  schemas:
 *    Seguidor:
 *      type: object
 *      properties:
 *        idUsuario:
 *          type: integer
 *          description: id de un Usuario
 *        idSeguidor:
 *          type: integer
 *          description: id del usuario que toma el rol de seguidor
 *      required:
 *        - idUsuario
 *        - idSeguidor
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
 *  name: Seguidor
 *  description: endpoints para Seguidor
 */

/**
 * @swagger
 * /Seguidor:
 *  get:
 *    summary: Permite consultar la lista de todos los Usuarios y sus Seguidores
 *    tags: [Seguidor]
 *    responses:
 *      200:
 *        description: Lista de Seguidor
 *        content:
 *          application/json:
 *            schema:
 *              type: array
 *              items:
 *                $ref: '#/components/schemas/Seguidor'
 */
ruta.get('/Seguidor', verificarToken, (req, res) => {
  mysqlConnection.query('CALL R_Seguidor()', (err, rows, fields) => {
    if (!err) {
      res.status(200).json(rows[0])
    } else {
      console.log(err)
    }
  })
})

/**
 * @swagger
 * /Seguidor/{idUsuario}/{idSeguidor}:
 *  get:
 *    summary: Permite comprobar si un Usuario esta siguiendo a otro
 *    tags: [Seguidor]
 *    responses:
 *      200:
 *        description: Usuario seguido
 */
ruta.get('/Seguidor/:idUsuario/:idSeguidor', verificarToken, (req, res) => {
  const { idUsuario, idSeguidor } = req.params
  mysqlConnection.query('CALL R_IsFollowing(?, ?)', [idUsuario, idSeguidor], (err, rows, fields) => {
    if (!err) {
      res.status(200).json(rows[0][0])
    } else {
      res.status(500)
    }
  })
})

/**
 * @swagger
 * /Seguidores/{idUsuario}:
 *  get:
 *    summary: Permite consultar la lista de Seguidores del Usuario que le mandemos
 *    tags: [Seguidor]
 *    parameters:
 *      - $ref: '#/components/parameters/idUsuario'
 *    responses:
 *      200:
 *        description: Lista de personas que sigue un usuario
 *        content:
 *          application/json:
 *            schema:
 *            $ref: '#/components/schemas/Seguidor'
 *      404:
 *        description: Usuario no encontrado
 */
ruta.get('/Seguidores/:idUsuario', verificarToken, (req, res) => {
  const { idUsuario } = req.params
  mysqlConnection.query('CALL R_Seguidores(?)', [idUsuario], (err, rows, fields) => {
    if (!err) {
      res.json(rows[0])
    } else {
      res.status(500)
    }
  })
})

/**
 * @swagger
 * /Seguidor/Siguiendo/{idUsuario}:
 *  get:
 *    summary: Permite consultar la lista de Usuarios que esta siguiendo el Usuario dado
 *    tags: [Seguidor]
 *    parameters:
 *      - $ref: '#/components/parameters/idUsuario'
 *    responses:
 *      200:
 *        description: Lista de seguidores del usuario mandado
 *        content:
 *          application/json:
 *            schema:
 *            $ref: '#/components/schemas/Seguidor'
 *      404:
 *        description: Usuario no encontrado
 */
ruta.get('/Siguiendo/:idUsuario', verificarToken, (req, res) => {
  const { idUsuario } = req.params
  mysqlConnection.query('CALL R_Siguiendo(?)', [idUsuario], (err, rows, fields) => {
    if (!err) {
      res.json(rows[0])
    } else {
      res.status(500)
    }
  })
})

/**
 * @swagger
 * /Seguidor:
 *  post:
 *    summary: Permite dar un follow
 *    tags: [Seguidor]
 *    requestBody:
 *      required: true
 *      content:
 *        application/json:
 *          schema:
 *            $ref: '#/components/schemas/Seguidor'
 *    responses:
 *      200:
 *        description: Follow
 *      500:
 *        description: Error con el servidor
 *
 */
ruta.post('/Seguidor', verificarToken, (req, res) => {
  const { idUsuario, idSeguidor } = req.body
  mysqlConnection.query('CALL C_Seguidor(?, ?)', [idUsuario, idSeguidor], (err, rows, fields) => {
    if (!err) {
      res.status(201).json(rows[0][0])
    } else {
      res.status(500)
    }
  })
})

/**
 * @swagger
 * /Seguidor:
 *  delete:
 *    summary: Permite quitar un follow
 *    tags: [Seguidor]
 *    requestBody:
 *      required: true
 *      content:
 *        application/json:
 *          schema:
 *            $ref: '#/components/schemas/Seguidor'
 *    responses:
 *      200:
 *        description: Follow quitado
 *      404:
 *        description: este follow no existe
 */
ruta.delete('/Seguidor/:idUsuario/:idSeguidor', verificarToken, (req, res) => {
  const { idUsuario, idSeguidor } = req.params
  mysqlConnection.query('CALL D_Unfollow(?, ?)', [idUsuario, idSeguidor], (err, rows, fields) => {
    if (!err) {
      res.status(200).json(rows[0][0])
    } else {
      res.status(500)
    }
  })
})
module.exports = ruta
