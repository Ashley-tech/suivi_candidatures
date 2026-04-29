<?php

namespace App\Mail;

use Illuminate\Mail\Mailable;

class TestMail extends Mailable
{
    public $subjectText;
    public $contentText;

    public function __construct($subjectText, $contentText)
    {
        $this->subjectText = $subjectText;
        $this->contentText = $contentText;
    }

    public function build()
    {
        return $this->subject($this->subjectText)
                    ->view('emails.test')
                    ->with([
                        'contentText' => $this->contentText
                    ]);
    }
}