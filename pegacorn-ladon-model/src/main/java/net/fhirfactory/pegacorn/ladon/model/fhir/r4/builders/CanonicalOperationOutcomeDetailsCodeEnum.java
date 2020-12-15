/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.ladon.model.fhir.r4.builders;


/**
 * see http://terminology.hl7.org/CodeSystem/operation-outcome (values)
 *
 */
public enum CanonicalOperationOutcomeDetailsCodeEnum {
    DELETE_MULTIPLE_MATCHES,
    MSG_AUTH_REQUIRED,
    MSG_BAD_FORMAT,
    MSG_BAD_SYNTAX,
    MSG_CANT_PARSE_CONTENT,
    MSG_CANT_PARSE_ROOT,
    MSG_CREATED,
    MSG_DATE_FORMAT,
    MSG_DELETED,
    MSG_DELETED_DONE,
    MSG_DELETED_ID,
    MSG_DUPLICATE_ID,
    MSG_ERROR_PARSING,
    MSG_ID_INVALID,
    MSG_ID_TOO_LONG,
    MSG_INVALID_ID,
    MSG_JSON_OBJECT,
    MSG_LOCAL_FAIL,
    MSG_NO_EXIST,
    MSG_NO_MATCH,
    MSG_NO_MODULE,
    MSG_NO_SUMMARY,
    MSG_OP_NOT_ALLOWED,
    MSG_PARAM_CHAINED,
    MSG_PARAM_INVALID,
    MSG_PARAM_MODIFIER_INVALID,
    MSG_PARAM_NO_REPEAT,
    MSG_PARAM_UNKNOWN,
    MSG_RESOURCE_EXAMPLE_PROTECTED,
    MSG_RESOURCE_ID_FAIL,
    MSG_RESOURCE_ID_MISMATCH,
    MSG_RESOURCE_ID_MISSING,
    MSG_RESOURCE_NOT_ALLOWED,
    MSG_RESOURCE_REQUIRED,
    MSG_RESOURCE_TYPE_MISMATCH,
    MSG_SORT_UNKNOWN,
    MSG_TRANSACTION_DUPLICATE_ID,
    MSG_TRANSACTION_MISSING_ID,
    MSG_UNHANDLED_NODE_TYPE,
    MSG_UNKNOWN_CONTENT,
    MSG_UNKNOWN_OPERATION,
    MSG_UNKNOWN_TYPE,
    MSG_UPDATED,
    MSG_VERSION_AWARE,
    MSG_VERSION_AWARE_CONFLICT,
    MSG_VERSION_AWARE_URL,
    MSG_WRONG_NS,
    SEARCH_MULTIPLE,
    SEARCH_NONE,
    UPDATE_MULTIPLE_MATCHES
}
