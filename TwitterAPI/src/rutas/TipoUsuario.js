const express = require('express')
const ruta = express.Router()
const mysqlConnection = require('../database')

/**
 * @swagger
 * components:
 *  schemas:
 *    TipoUsuario:
 *      type: object
 *      properties:
 *        idTipoUsuario:
 *          type: integer
 *          description: id autogenerado de TipoUsuario
 *        TipoUsuario:
 *          type: string
 *          description: el rol que representa un usuario en el sistema
 *      required:
 *        - TipoUsuario
 *  parameters:
 *    idTipoUsuario:
 *      in: path
 *      name: idTipoUsuario
 *      required: true
 *      schema:
 *        type: integer
 *      description: id del TipoUsuario
 */

/**
 * @swagger
 * tags:
 *  name: TipoUsuario
 *  description: endpoints para TipoUsuario
 */

/**
 * @swagger
 * /TipoUsuario:
 *  get:
 *    summary: Permite consultar la lista de Tipos de usuario que existen en el sistema
 *    tags: [TipoUsuario]
 *    responses:
 *      200:
 *        description: Lista de TipoUsuario
 *        content:
 *          application/json:
 *            schema:
 *              type: array
 *              items:
 *                $ref: '#/components/schemas/TipoUsuario'
 */
ruta.get('/TipoUsuario', (req, res) => {
  mysqlConnection.query('CALL R_TipoUsuario()', (err, rows, fields) => {
    if (!err) {
      res.json(rows[0])
    } else {
      console.log(err)
    }
  })
})

/**
 * @swagger
 * /TipoUsuario:
 *  post:
 *    summary: Permite crear un nuevo Tipo de usuario
 *    tags: [TipoUsuario]
 *    requestBody:
 *      required: true
 *      content:
 *        application/json:
 *          schema:
 *            $ref: '#/components/schemas/TipoUsuario'
 *    responses:
 *      200:
 *        description: TipoUsuario guardado
 *      500:
 *        description: Error con el servidor
 *
 */

ruta.post('/TipoUsuario', (req, res) => {
  const { TipoUsuario } = req.body
  mysqlConnection.query('CALL CU_TipoUsuario(?, ?)', [0, TipoUsuario], (err, rows, fields) => {
    if (!err) {
      res.json({ Status: 'TipoUsuario guardado' })
    } else {
      console.log(err)
    }
  })
})

/**
 * @swagger
 * /TipoUsuario/{idTipoUsuario}:
 *  put:
 *    summary: Permite consultar un Tipo de Usuario
 *    tags: [TipoUsuario]
 *    parameters:
 *      - $ref: '#/components/parameters/idTipoUsuario'
 *    requestBody:
 *      required: true
 *      content:
 *        application/json:
 *          schema:
 *            $ref: '#/components/schemas/TipoUsuario'
 *    responses:
 *      200:
 *        description: TipoUsuario actualizado
 *      404:
 *        description: TipoUsuario no encontrado
 */
ruta.put('/TipoUsuario/:idTipoUsuario', (req, res) => {
  const { TipoUsuario } = req.body
  const { idTipoUsuario } = req.params
  mysqlConnection.query('CALL CU_TipoUsuario(?, ?)', [idTipoUsuario, TipoUsuario], (err, rows, fields) => {
    if (!err) {
      res.json({ Status: 'TipoUsuario actualizado' })
    } else {
      console.log(err)
    }
  })
})

/**
 * @swagger
 * /TipoUsuario/{idTipoUsuario}:
 *  delete:
 *    summary: Permite eliminar un Tipo de usuario
 *    tags: [TipoUsuario]
 *    parameters:
 *      - $ref: '#/components/parameters/idTipoUsuario'
 *    responses:
 *      200:
 *        description: TipoUsuario eliminado
 *      404:
 *        description: TipoUsuario no encontrado
 */
ruta.delete('/TipoUsuario/:idTipoUsuario', (req, res) => {
  const { idTipoUsuario } = req.params
  mysqlConnection.query('CALL D_TipoUsuario(?)', [idTipoUsuario], (err, rows, fields) => {
    if (!err) {
      res.json({ Status: 'TipoUsuario eliminados' })
    } else {
      console.log(err)
    }
  })
})

module.exports = ruta
