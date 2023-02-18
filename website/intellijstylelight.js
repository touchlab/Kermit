var colors = {
    function: "#01627a",
    comment: "#777777",
    char: "#917a04",
    keyword: "#0033b2",
    boolean: "#75461b",
    primitive: "#6897bb",
    string: "#087d18",
    variable: "#a9b7c6",
    variable2: "#9876aa",
    className: "#a9b7c6",
    method: "#01627a",

    //Don't know what these should be...
    punctuation: "#080808",
    tag: "#ff0000",
    operator: "#ff0000"
};

const theme = {
    plain: {
        backgroundColor: "#ffffff",
        color: "#000000"
    },
    styles: [{
        types: ["attr-name"],
        style: {
            color: colors.keyword
        }
    }, {
        types: ["attr-value"],
        style: {
            color: colors.string
        }
    }, {
        types: ["comment", "block-comment", "prolog", "doctype", "cdata", "shebang"],
        style: {
            color: colors.comment
        }
    }, {
        types: ["property", "number", "function-name", "constant", "symbol", "deleted"],
        style: {
            color: colors.primitive
        }
    }, {
        types: ["boolean"],
        style: {
            color: colors.boolean
        }
    }, {
        types: ["tag"],
        style: {
            color: colors.tag
        }
    }, {
        types: ["string"],
        style: {
            color: colors.string
        }
    }, {
        types: ["punctuation"],
        style: {
            color: colors.punctuation
        }
    }, {
        types: ["selector", "char", "builtin", "inserted"],
        style: {
            color: colors.char
        }
    }, {
        types: ["function"],
        style: {
            color: colors.function
        }
    }, {
        types: ["operator", "entity", "url"],
        style: {
            color: colors.variable
        }
    }, {
        types: ["variable", "property", "constant", "delimiter"],
        style: {
            color: colors.variable2
        }
    }, {
        types: ["keyword"],
        style: {
            color: colors.keyword
        }
    }, {
        types: ["at-rule", "class-name"],
        style: {
            color: colors.className
        }
    }, {
        types: ["important"],
        style: {
            fontWeight: "400"
        }
    }, {
        types: ["bold"],
        style: {
            fontWeight: "bold"
        }
    }, {
        types: ["italic"],
        style: {
            fontStyle: "italic"
        }
    }, {
        types: ["namespace"],
        style: {
            opacity: 0.7
        }
    }]
};

// export default theme;

module.exports = theme;