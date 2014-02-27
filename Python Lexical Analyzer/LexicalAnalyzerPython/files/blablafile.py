def srep(p):
    '''Print the coefficient list as a polynomial.'''

    # Get the exponent of first coefficient, plus 1.
    exp = len(p)

    # Go through the coefs and turn them into terms.
    retval = ''
    while p:
        # Adjust exponent.  Done here so continue will run it.
        exp = exp - 1

        coef = p[0]
        p = p[1:]

        # If zero, leave it out.
        if coef == 0: continue

        # If adding, need a + or -.
        if retval:
            if coef >= 0:
                retval = retval + ' + '
            else:
                coef = -coef
                retval = retval + ' - '

        # Add the coefficient, if needed.
        if coef != 1 or exp == 0:
            retval = retval + str(coef)
            if exp != 0: retval = retval + '*'

        # Put the x, if we need it.
        if exp != 0:
            retval = retval + 'x'
            if exp != 1: retval = retval + '^' + str(exp)

    # For zero, say that.
    if not retval: retval = '0'

    return retval
