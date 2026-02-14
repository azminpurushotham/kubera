package com.collection.kubera.data.repository

object RepositoryConstants {
    const val SHOP_PAGE_SIZE = 10
    const val MIN_SEARCH_QUERY_LENGTH = 2
    const val SEARCH_DEBOUNCE_MS = 300L

    const val DEFAULT_ERROR_MESSAGE = "Something went wrong. Please refresh the page."

    const val TRANSACTION_HISTORY_PAGE_SIZE = 10

    const val REPORT_ALL_SHOPS_FILENAME = "AllShops.csv"
    const val REPORT_DIR_NOT_CREATED = "Directories not created"
    const val REPORT_INVALID_DATE_FORMAT = "Invalid date format"

    const val ADD_SHOP_SUCCESS_MESSAGE = "New Shop Added Successfully"
    const val ADD_SHOP_ERROR_MESSAGE = "Shop is not added, please try again"

    const val UPDATE_SHOP_SUCCESS_MESSAGE = "Shop details updated successfully"
    const val UPDATE_SHOP_ERROR_MESSAGE = "Shop details is not updated, please try again"
    const val COLLECTION_HISTORY_NOT_UPDATED = "Collection history not updated"

    const val PROFILE_USER_DETAILS_ERROR = "Something went wrong with this user details"
    const val PROFILE_CREDENTIALS_UPDATED = "Credentials updated successfully"
    const val PROFILE_UPDATE_FAILED = "Failed to update credentials"
    const val PROFILE_USERNAME_EMPTY = "Username cannot be empty"
    const val PROFILE_PASSWORD_EMPTY = "Password cannot be empty"
    const val PROFILE_PASSWORD_MISMATCH = "Passwords do not matching"

    const val LOGIN_SUCCESS_MESSAGE = "Successfully logged in"
    const val LOGIN_CREDENTIALS_ERROR = "Please enter correct credentials"
}
