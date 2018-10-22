        jQuery.browser = jQuery.browser || {};
        (function () {
            jQuery.browser.msie = jQuery.browser.msie || false;
            jQuery.browser.version = jQuery.browser.version || 0;
            if (navigator.userAgent.match(/MSIE ([0-9]+)\./)) {
                jQuery.browser.msie = true;
                jQuery.browser.version = RegExp.$1;
            }
        })();

        $(function () {
            var url = window.location.search.match(/url=([^&]+)/);
            if (url && url.length > 1) {
                url = decodeURIComponent(url[1]);
            } else {
                url = "<?php echo $this->api_spec?>";
            }

            window.swaggerUi = new SwaggerUi({
                url: url,
                dom_id: "swagger-ui-container",
                supportedSubmitMethods: ['get', 'post', 'put', 'delete', 'patch'],
                onComplete: function (swaggerApi, swaggerUi) {
                    if (typeof initOAuth == "function") {

                        initOAuth({
                            clientId: "ffe7748a-3a3f-4860-a02a-42ab08e4fde2",
                            realm: "realm",
                            appName: "Swagger"
                        });

                    }

                    $('pre code').each(function (i, e) {
                        hljs.highlightBlock(e)
                    });

                    swaggerUi.options.host = '<?php echo BASE_URL; ?>';

                    if (swaggerUi.options.url) {
                        $('#input_baseUrl').val(swaggerUi.options.url);
                    }
                    if (swaggerUi.options.apiKey) {
                        $('#input_apiKey').val(swaggerUi.options.apiKey);
                    }

                    $("[data-toggle='tooltip']").tooltip();

                    addApiKeyAuthorization();
                },
                onFailure: function (data) {
                    log("Unable to Load SwaggerUI");
                },
                docExpansion: "none",
                operationsSorter: function (a, b) {
                var order = { 'get': '0', 'post': '1', 'put': '2', 'delete': '3' };
                return order[a.method].localeCompare(order[b.method]);
              },
            });


            function addApiKeyAuthorization() {
                var key = encodeURIComponent($('#input_apiKey')[0].value);
                if (key && key.trim() != "") {
                    var apiKeyAuth = new SwaggerClient.ApiKeyAuthorization("Authorization", "Bearer " + key, "header");
                    window.swaggerUi.api.clientAuthorizations.add("api_key", apiKeyAuth);
                    log("added key " + key);
                }
                <?php if(isset($this->api_key) and $this->api_key){ ?>
                  $('input[name="api_key"]').val('<?php echo $this->api_key; ?>');
                  $('*[data-label="api_key"]').addClass('hide');
                <?php } ?>
            }

            $('#input_apiKey').change(addApiKeyAuthorization);
            // if you have an apiKey you would like to pre-populate on the page for demonstration purposes...
            <?php if(isset($this->api_key) and $this->api_key){ ?>
             var apiKey = "<?php echo $this->api_key; ?>";
             $('#input_apiKey').val(apiKey);
            <?php } ?>

            window.swaggerUi.load();

            function log() {
                if ('console' in window) {
                    console.log.apply(console, arguments);
                }
            }
            $('#resources_container').css('background-color', 'rgba(0,0,0,0.1)');
            $('label[for=input-api-token]').val('Version');

        });

        $(function () {

            $(window).scroll(function () {
                var sticky = $(".sticky-nav");

                i(sticky);
                r(sticky);

                function n() {
                    return window.matchMedia("(min-width: 992px)").matches
                }

                function e() {
                    n() ? sticky.parents(".sticky-nav-placeholder").removeAttr("style") : sticky.parents(".sticky-nav-placeholder").css("min-height", sticky.outerHeight())
                }

                function i(n) {
                    n.hasClass("fixed") || (navOffset = n.offset().top);
                    e();
                    $(window).scrollTop() > navOffset ? $(".modal.in").length || n.addClass("fixed") : n.removeClass("fixed")
                }

                function r(e) {
                    function i() {
                        var i = $(window).scrollTop(), r = e.parents(".sticky-nav");
                        return r.hasClass("fixed") && !n() && (i = i + r.outerHeight() + 40), i
                    }

                    function r(e) {
                        var t = o.next("[data-endpoint]"), n = o.prev("[data-endpoint]");
                        return "next" === e ? t.length ? t : o.parent().next().find("[data-endpoint]").first() : "prev" === e ? n.length ? n : o.parent().prev().find("[data-endpoint]").last() : []
                    }

                    var nav = e.find("[data-navigator]");
                    if (nav.find("[data-endpoint][data-selected]").length) {
                        var o = nav.find("[data-endpoint][data-selected]"),
                            a = $("#" + o.attr("data-endpoint")),
                            s = a.offset().top,
                            l = (s + a.outerHeight(), r("next")),
                            u = r("prev");
                        if (l.length) {
                            {
                                var d = $("#" + l.attr("data-endpoint")), f = d.offset().top;
                                f + d.outerHeight()
                            }
                            i() >= f && c(l)
                        }
                        if (u.length) {
                            var p = $("#" + u.attr("data-endpoint")),
                            g = u.offset().top;
                            v = (g + p.outerHeight(), 100);
                            i() < s - v && c(u)
                        }
                    }
                }

                function s() {
                    var e = $(".sticky-nav [data-navigator]"),
                        n = e.find("[data-endpoint]").first();
                    n.attr("data-selected", "");
                    u.find("[data-selected-value]").html(n.text())
                }

                function c(e) {
                    {
                        var n = $(".sticky-nav [data-navigator]");
                        $("#" + e.attr("data-endpoint"))
                    }
                    n.find("[data-resource]").removeClass("active");
                    n.find("[data-selected]").removeAttr("data-selected");
                    e.closest("[data-resource]").addClass("active");
                    e.attr("data-selected", "");
                    sticky.find("[data-selected-value]").html(e.text())
                }
            });

        });

        $(function () {
            $("[data-toggle='tooltip']").tooltip();
        });
