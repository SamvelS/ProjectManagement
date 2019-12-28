$(function() {
    preparePasswordValidation();

    function preparePasswordValidation() {
        $('#changePasswordForm').validate({
            rules: {
                oldPassword: {
                    required: true,
                    minlength: 6,
                    maxlength: 50
                },
                newPassword: {
                    required: true,
                    minlength: 6,
                    maxlength: 50
                },
                confirmPassword: {
                    required: true,
                    minlength: 6,
                    maxlength: 50,
                    equalTo: "#newPassword"
                }
            },
            messages: {
                oldPassword: "Password length should be between 6 and 50",
                newPassword: "Password length should be between 6 and 50",
                confirmPassword: "Enter Confirm Password Same as New Password"
            }
        });
    }

    $('#button').click(function(event){
        event.preventDefault();
        if($('#changePasswordForm').valid()) {
            changePassword();
        }
    });

    async function changePassword() {
        const data = {
            oldPassword: $('#oldPassword').val(),
            newPassword: $('#newPassword').val(),
            confirmPassword: $('#confirmPassword').val()
        };

        $('span.validation-error').remove();
        $('div.validation-error-container').remove();

        try {
            const response = await axios({
                method: 'post',
                url: '/users/changePassword',
                data
            });

            location.replace("/logout");
        }
        catch (error) {
            if(error.response) {
                if(error.response.status == 400) {

                    error.response.data.forEach((item, index) => {
                        if($("#" + item.fst).length > 0) {
                            $("<div class='validation-error-container'><span class='validation-error'>" + item.snd + "</span></div>").insertBefore($("#" + item.fst));
                        }
                    });
                }
            }
            else {
                console.log(error);
            }
        }
    }
});